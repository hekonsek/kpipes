package net.kpipes.core

import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

interface FunctionBuilder<T> extends net.kpipes.core.function.FunctionBuilder {

    boolean supports(Object function)

    void build(PipeDefinition pipeDefinition, T function, KStream<String, Bytes> source)

    void build(PipeDefinition pipeDefinition, T function, KTable<String, Bytes> source)


}