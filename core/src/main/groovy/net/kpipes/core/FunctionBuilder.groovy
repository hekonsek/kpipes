package net.kpipes.core

import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream

interface FunctionBuilder<T> {

    boolean supports(Object function)

    void build(PipeDefinition pipeDefinition, T function, KStream<String, Bytes> source)

}