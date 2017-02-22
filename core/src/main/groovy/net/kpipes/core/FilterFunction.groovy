package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

class FilterFunction implements EventTableFunction {

    @Override
    void apply(PipeDefinition pipeDefinition, KTable<String, Bytes> source) {
        def predicateText = pipeDefinition.functionConfiguration().predicate as String
        source.filter { Object key, Object value ->
            def shell = new GroovyShell()
            shell.setVariable('key', key)
            def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
            shell.setVariable('event', event)
            shell.evaluate(predicateText) as boolean
        }.to(pipeDefinition.to().get())
    }

    @Override
    void apply(PipeDefinition pipeDefinition, KStream<String, Bytes> source) {
        def predicateText = pipeDefinition.functionConfiguration().predicate as String
        source.filter { Object key, Object value ->
            def shell = new GroovyShell()
            shell.setVariable('key', key)
            def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
            shell.setVariable('event', event)
            shell.evaluate(predicateText) as boolean
        }.to(pipeDefinition.to().get())
    }

}