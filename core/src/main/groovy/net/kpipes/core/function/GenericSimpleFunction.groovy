package net.kpipes.core.function

import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition

interface GenericSimpleFunction {

    void apply(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition)

}