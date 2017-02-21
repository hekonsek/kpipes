package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Aggregator
import org.apache.kafka.streams.kstream.Initializer
import org.apache.kafka.streams.kstream.KTable

class EventAggregateFunctionBuilder implements FunctionBuilder<EventAggregateFunction> {

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
                event = function.onEvent(true, config, aggregate as Map, aggKey as String, event)
                new Bytes(new ObjectMapper().writeValueAsBytes(event))
            }
        }, new Aggregator() {
            @Override
            Object apply(Object aggKey, Object value, Object aggregate) {
                if(aggregate instanceof Bytes) {
                    aggregate = new ObjectMapper().readValue((aggregate as Bytes).get(), Map)
                }
                def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
                event = function.onEvent(false, config, aggregate as Map, aggKey as String, event)
                new Bytes(new ObjectMapper().writeValueAsBytes(event))
            }
        }, Serdes.Bytes(), "${pipeDefinition.from()}${pipeDefinition.to().get()}").to(pipeDefinition.to().get())
    }

}