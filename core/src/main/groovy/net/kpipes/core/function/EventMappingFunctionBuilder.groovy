package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipes
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.TopologyBuilderException
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.AbstractProcessor
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.processor.TopologyBuilder

class EventMappingFunctionBuilder implements TopologyFunctionBuilder<EventMappingFunction> {

    private final KPipes kpipes

    EventMappingFunctionBuilder(KPipes kpipes) {
        this.kpipes = kpipes
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
        def sourceId = pipeDefinition.effectiveFrom()
        def processorId = (topologyBuilder as KStreamBuilder).newName('processor')
        def targetId = (topologyBuilder as KStreamBuilder).newName('target')
        try {
            topologyBuilder.addSource(sourceId, pipeDefinition.effectiveFrom())
        } catch (TopologyBuilderException e) {

        }
        topologyBuilder.addProcessor(processorId, new ProcessorSupplier<String, Bytes>() {
                    @Override
                    Processor<String, Bytes> get() {
                        new AbstractProcessor<String, Bytes>() {
                            @Override
                            void process(String key, Bytes value) {
                                def event = new ObjectMapper().readValue(value.get(), Map)
                                value = new Bytes(new ObjectMapper().writeValueAsBytes(function.onEvent(new Event(context().topic(), key, event, pipeDefinition.functionConfiguration(), kpipes))))
                                context().forward(key, value)
                            }
                        }
                    }
                }, sourceId).
                addSink(targetId, pipeDefinition.effectiveTo().get(), processorId)
    }

}