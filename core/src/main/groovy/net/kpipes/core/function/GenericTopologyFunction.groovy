package net.kpipes.core.function

import net.kpipes.core.PipeDefinition
import org.apache.kafka.streams.processor.TopologyBuilder

interface GenericTopologyFunction {

    void apply(PipeDefinition pipeDefinition, TopologyBuilder topologyBuilder)

}