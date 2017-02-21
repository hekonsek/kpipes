package net.kpipes.core

import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

class EventStreamFunctionBuilder implements FunctionBuilder<EventStreamFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof EventStreamFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventStreamFunction function, KTable<String, Bytes> source) {
        function.apply(pipeDefinition, source)
    }

}