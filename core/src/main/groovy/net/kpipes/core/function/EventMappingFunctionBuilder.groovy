package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.errors.TopologyBuilderException
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.AbstractProcessor
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.processor.TopologyBuilder

class EventMappingFunctionBuilder implements TopologyFunctionBuilder<EventMappingFunction> {

    private final KPipesContext kPipesContext

    EventMappingFunctionBuilder(KPipesContext kPipesContext) {
        this.kPipesContext = kPipesContext
    }

    @Override
    boolean supports(Object function) {
        function instanceof EventMappingFunction
    }

    @Override
    void build(PipeBuilder pipeBuilder, TopologyBuilder topologyBuilder, PipeDefinition pipeDefinition, EventMappingFunction function) {
        if(function instanceof FunctionInitializer) {
            function.initialize(pipeBuilder, pipeDefinition)
        }
        def sourceId = pipeDefinition.from()
        def processorId = (topologyBuilder as KStreamBuilder).newName('processor')
        def targetId = (topologyBuilder as KStreamBuilder).newName('target')
        try {
            topologyBuilder.addSource(sourceId, pipeDefinition.from())
        } catch (TopologyBuilderException e) {

        }
        topologyBuilder.addProcessor(processorId, new ProcessorSupplier<String, Bytes>() {
                    @Override
                    Processor<String, Bytes> get() {
                        new AbstractProcessor<String, Bytes>() {
                            @Override
                            void process(String key, Bytes value) {
                                def event = new ObjectMapper().readValue(value.get(), Map)
                                value = new Bytes(new ObjectMapper().writeValueAsBytes(function.onEvent(new Event(context().topic(), key, event, pipeDefinition.functionConfiguration(), kPipesContext))))
                                context().forward(key, value)
                            }
                        }
                    }
                }, sourceId).
                addSink(targetId, pipeDefinition.to().get(), processorId)
    }

}