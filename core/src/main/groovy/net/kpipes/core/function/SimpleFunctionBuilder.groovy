package net.kpipes.core.function

import net.kpipes.core.KPipes
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition

interface SimpleFunctionBuilder<T> extends FunctionBuilder {

    void build(KPipes kpipes, PipeDefinition pipeDefinition, T function)

}