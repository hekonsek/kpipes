package net.kpipes.core.function

import net.kpipes.core.KPipes
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition

interface FunctionInitializer {

    void initialize(KPipes kipes, PipeDefinition pipeDefinition)

}