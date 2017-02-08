package net.kpipes.core.function.pipe

import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.spi.Service

class PipeBuilderConfig {

    @Service
    PipeBuilder pipeBuilder(KPipes kpipes) {
        new PipeBuilder(kpipes)
    }

    @Service(onStart = 'start', overridable = true)
    FunctionExecutor functionExecutor(KPipes kpipes) {
        new KafkaFunctionExecutor(kpipes)
    }

}
