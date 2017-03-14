package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Aggregator
import org.apache.kafka.streams.kstream.Initializer
import org.apache.kafka.streams.kstream.KTable

class EventAggregateFunctionBuilder implements TableFunctionBuilder<EventAggregateFunction> {

    private final KPipesContext kpipesContext

    EventAggregateFunctionBuilder(KPipesContext kpipesContext) {
        this.kpipesContext = kpipesContext
    }

    @Override
    boolean supports(Object function) {
        function instanceof EventAggregateFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventAggregateFunction function, KTable<String, Bytes> source) {
        def config = pipeDefinition.functionConfiguration()
        source.groupBy { String key, Bytes value ->
            def groupBy = config.groupBy as String
            groupBy = new ObjectMapper().readValue((value as Bytes).get(), Map)[groupBy]
            new KeyValue<>(groupBy, value)
        }.aggregate(new Initializer() {
            @Override
            Object apply() {
                [:]
            }
        }, new Aggregator() {
            @Override
            Object apply(Object aggKey, Object value, Object aggregate) {
                if(aggregate instanceof Bytes) {
                    aggregate = new ObjectMapper().readValue((aggregate as Bytes).get(), Map)
                }
                def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
                event = function.onEvent(new Event(null, aggKey as String, event, config, true, kpipesContext), aggregate as Map)
                new Bytes(new ObjectMapper().writeValueAsBytes(event))
            }
        }, new Aggregator() {
            @Override
            Object apply(Object aggKey, Object value, Object aggregate) {
                if(aggregate instanceof Bytes) {
                    aggregate = new ObjectMapper().readValue((aggregate as Bytes).get(), Map)
                }
                def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
                event = function.onEvent(new Event(null, aggKey as String, event, config, false, kpipesContext), aggregate as Map)
                new Bytes(new ObjectMapper().writeValueAsBytes(event))
            }
        }, Serdes.Bytes(), "${pipeDefinition.effectiveFrom()}${pipeDefinition.effectiveTo().get()}").to(pipeDefinition.effectiveTo().get())
    }

}