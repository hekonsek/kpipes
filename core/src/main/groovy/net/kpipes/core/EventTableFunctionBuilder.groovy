package net.kpipes.core

import org.apache.commons.lang3.NotImplementedException
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

class EventTableFunctionBuilder implements FunctionBuilder<EventTableFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof EventTableFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventTableFunction function, KStream<String, Bytes> source) {
        function.apply(pipeDefinition, source)
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventTableFunction function, KTable<String, Bytes> source) {
        function.apply(pipeDefinition, source)
    }

}