package net.kpipes.function.view.materialize.keyvalue

import net.kpipes.core.KPipes
import net.kpipes.core.PipeDefinition
import net.kpipes.core.function.GenericSimpleFunction
import net.kpipes.core.store.ViewMaterializer

class MaterializeKeyValueViewFunction implements GenericSimpleFunction {

    @Override
    void apply(KPipes kpipes, PipeDefinition pipeDefinition) {
        kpipes.serviceRegistry().service(ViewMaterializer).materialize("pipe_${pipeDefinition.id()}", pipeDefinition.effectiveFrom())
    }

}