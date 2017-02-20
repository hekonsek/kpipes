package net.kpipes.core

import groovy.transform.CompileStatic

@CompileStatic
class KPipes {

    private final ServiceRegistry serviceRegistry

    private final PipeBuilder pipeBuilder

    KPipes(KPipesConfig config, ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry
        this.pipeBuilder = new PipeBuilder(config, serviceRegistry)
    }

    void start() {
        pipeBuilder.start()
    }

    PipeBuilder pipeBuilder() {
        pipeBuilder
    }

    ServiceRegistry serviceRegistry() {
        serviceRegistry
    }

}