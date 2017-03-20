package net.kpipes.function.view.materialize.keyvalue

import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import net.kpipes.core.function.GenericTopologyFunction
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.TopologyBuilder

class MaterializeKeyValueViewFunction implements GenericTopologyFunction {

    @Override
    void apply(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition, TopologyBuilder topologyBuilder) {
        (topologyBuilder as KStreamBuilder).globalTable(pipeDefinition.effectiveFrom(), pipeDefinition.effectiveFrom())
    }

}
