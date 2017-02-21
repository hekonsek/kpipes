package net.kpipes.core

import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

interface EventStreamFunction {

    void apply(PipeDefinition pipeDefinition, KTable<String, Bytes> source)

}