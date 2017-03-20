package net.kpipes.functions.filter

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition

import net.kpipes.core.function.GenericTopologyFunction
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.TopologyBuilder

class FilterFunction implements GenericTopologyFunction {

    @Override
    void apply(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition, TopologyBuilder topologyBuilder) {
        def predicateText = pipeDefinition.functionConfiguration().predicate as String
        def source = pipeBuilder.@sourceStreams[pipeDefinition.effectiveFrom()]
        if(source == null) {
            source = (topologyBuilder as KStreamBuilder).stream(pipeDefinition.effectiveFrom())
            pipeBuilder.@sourceStreams[pipeDefinition.effectiveFrom()] = source
        }
        source.filter { Object key, Object value ->
            def shell = new GroovyShell()
            shell.setVariable('key', key)
            def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
            shell.setVariable('event', event)
            shell.evaluate(predicateText) as boolean
        }.to(pipeDefinition.effectiveTo().get())
    }

}