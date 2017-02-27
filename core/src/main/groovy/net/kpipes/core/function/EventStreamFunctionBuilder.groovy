package net.kpipes.core.function

import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream

class EventStreamFunctionBuilder implements StreamFunctionBuilder<EventStreamFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof EventStreamFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventStreamFunction function, KStream<String, Bytes> source) {
        function.apply(pipeDefinition, source)
    }

}