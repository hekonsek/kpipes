package net.kpipes.core

import groovy.transform.CompileStatic

@CompileStatic
class KPipes {

    private final ServiceRegistry functionRegistry

    private final PipeBuilder pipeBuilder

    KPipes(KPipesConfig config, ServiceRegistry functionRegistry) {
        this.functionRegistry = functionRegistry
        this.pipeBuilder = new PipeBuilder(config, functionRegistry)
    }

    void start() {
        pipeBuilder.start()
    }

    PipeBuilder pipeBuilder() {
        pipeBuilder
    }

}