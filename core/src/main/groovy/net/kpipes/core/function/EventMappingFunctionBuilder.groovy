package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream

class EventMappingFunctionBuilder implements StreamFunctionBuilder<EventMappingFunction> {

    private final KPipesContext kPipesContext

    EventMappingFunctionBuilder(KPipesContext kPipesContext) {
        this.kPipesContext = kPipesContext
    }

    @Override
    boolean supports(Object function) {
        function instanceof EventMappingFunction
    }

    @Override
    void build(PipeDefinition pipeDefinition, EventMappingFunction function, KStream<String, Bytes> source) {
        source.map{ String key, Bytes value ->
            def event = new ObjectMapper().readValue(value.get(), Map)
            new KeyValue<>(key, new Bytes(new ObjectMapper().writeValueAsBytes(function.onEvent(new Event(key, event, pipeDefinition.functionConfiguration(), kPipesContext)))))
        }.to(pipeDefinition.to().get())
    }

}