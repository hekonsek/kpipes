package net.kpipes.core.function

import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import org.apache.kafka.streams.processor.TopologyBuilder

class GenericTopologyFunctionBuilder implements TopologyFunctionBuilder<GenericTopologyFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof GenericTopologyFunction
    }

    @Override
    void build(PipeBuilder pipeBuilder, TopologyBuilder topologyBuilder, PipeDefinition pipeDefinition, GenericTopologyFunction function) {
        function.apply(pipeBuilder, pipeDefinition, topologyBuilder)
    }

}