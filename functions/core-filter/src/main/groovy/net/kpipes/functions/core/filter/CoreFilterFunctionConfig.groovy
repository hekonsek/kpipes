package net.kpipes.functions.core.filter

import net.kpipes.core.function.FunctionBinding
import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.Service

class CoreFilterFunctionConfig {

    @Service
    FunctionBinding coreFilterFunctionBinding(KPipes kpipes) {
        new FunctionBinding(kpipes, 'core.filter', new CoreFilterFunction()).start()
    }

}
