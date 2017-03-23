package net.kpipes.core.function

import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition

class GenericSimpleFunctionBuilder implements SimpleFunctionBuilder<GenericSimpleFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof GenericSimpleFunction
    }

    @Override
    void build(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition, GenericSimpleFunction function) {
        function.apply(pipeBuilder, pipeDefinition)
    }

}
