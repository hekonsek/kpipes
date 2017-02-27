package net.kpipes.core.function

import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream

interface EventStreamFunction {

    void apply(PipeDefinition pipeDefinition, KStream<String, Bytes> source)

}