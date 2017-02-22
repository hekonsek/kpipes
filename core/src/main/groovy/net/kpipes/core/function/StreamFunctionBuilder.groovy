package net.kpipes.core.function

import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

interface StreamFunctionBuilder<T> extends FunctionBuilder {

    void build(PipeDefinition pipeDefinition, T function, KStream<String, Bytes> source)

}