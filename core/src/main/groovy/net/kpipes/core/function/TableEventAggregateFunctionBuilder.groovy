package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.PipeDefinition
import org.apache.commons.lang3.NotImplementedException
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Aggregator
import org.apache.kafka.streams.kstream.Initializer
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

class TableEventAggregateFunctionBuilder implements TableFunctionBuilder<EventAggregateFunction> {

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
            if(config.byTenant == null || (config.byTenant as boolean)) {
                def tenant = key.split(/\|/)[0]
                groupBy = "${tenant}|${groupBy}" as String
            }
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
                event = function.onEvent(new Event(aggKey as String, event, config, true), aggregate as Map)
                new Bytes(new ObjectMapper().writeValueAsBytes(event))
            }
        }, new Aggregator() {
            @Override
            Object apply(Object aggKey, Object value, Object aggregate) {
                if(aggregate instanceof Bytes) {
                    aggregate = new ObjectMapper().readValue((aggregate as Bytes).get(), Map)
                }
                def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
                event = function.onEvent(new Event(aggKey as String, event, config, false), aggregate as Map)
                new Bytes(new ObjectMapper().writeValueAsBytes(event))
            }
        }, Serdes.Bytes(), "${pipeDefinition.from()}${pipeDefinition.to().get()}").to(pipeDefinition.to().get())
    }

}