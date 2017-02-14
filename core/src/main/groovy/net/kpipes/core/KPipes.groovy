package net.kpipes.core

import groovy.transform.CompileStatic

@CompileStatic
class KPipes {

    private final FunctionRegistry functionRegistry

    private final PipeBuilder pipeBuilder

    KPipes(KPipesConfig config, FunctionRegistry functionRegistry) {
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