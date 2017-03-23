package net.kpipes.core.function

import net.kpipes.core.KPipes
import net.kpipes.core.PipeDefinition

class GenericSimpleFunctionBuilder implements SimpleFunctionBuilder<GenericSimpleFunction> {

    @Override
    boolean supports(Object function) {
        function instanceof GenericSimpleFunction
    }

    @Override
    void build(KPipes kpipes, PipeDefinition pipeDefinition, GenericSimpleFunction function) {
        function.apply(kpipes, pipeDefinition)
    }

}
