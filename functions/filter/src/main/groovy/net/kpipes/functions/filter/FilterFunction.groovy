package net.kpipes.functions.filter

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.PipeDefinition
import net.kpipes.core.function.EventStreamFunction
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream

class FilterFunction implements EventStreamFunction {

    @Override
    void apply(PipeDefinition pipeDefinition, KStream<String, Bytes> source) {
        def predicateText = pipeDefinition.functionConfiguration().predicate as String
        source.filter { Object key, Object value ->
            def shell = new GroovyShell()
            shell.setVariable('key', key)
            def event = new ObjectMapper().readValue((value as Bytes).get(), Map)
            shell.setVariable('event', event)
            shell.evaluate(predicateText) as boolean
        }.to(pipeDefinition.effectiveTo().get())
    }

}