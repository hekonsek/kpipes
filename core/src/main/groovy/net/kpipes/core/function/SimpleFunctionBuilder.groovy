package net.kpipes.core.function

import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition

interface SimpleFunctionBuilder<T> extends FunctionBuilder {

    void build(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition, T function)

}