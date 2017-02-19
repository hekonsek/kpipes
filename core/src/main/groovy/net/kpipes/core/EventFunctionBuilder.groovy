package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KeyValueMapper

class EventFunctionBuilder implements FunctionBuilder<EventFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof EventFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventFunction function, KStream<String, Bytes> source) {
        source.map(new KeyValueMapper<String, Bytes, KeyValue>() {
            @Override
            KeyValue apply(String key, Bytes value) {
                def event = new ObjectMapper().readValue(value.get(), Map)
                new KeyValue<>(key, new Bytes(new ObjectMapper().writeValueAsBytes(function.apply(pipeDefinition.functionConfiguration(), key, event))))
            }
        }).to(pipeDefinition.to().get())
    }

}