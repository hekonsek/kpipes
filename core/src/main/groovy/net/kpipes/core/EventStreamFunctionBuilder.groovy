package net.kpipes.core

import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream

class EventStreamFunctionBuilder implements FunctionBuilder<EventStreamFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof EventStreamFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventStreamFunction function, KStream<String, Bytes> source) {
        function.apply(pipeDefinition, source)
    }

}