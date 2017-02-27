package net.kpipes.core

import groovy.transform.CompileStatic

@CompileStatic
class KPipes {

    private final ServiceRegistry serviceRegistry

    private final PipeBuilder pipeBuilder

    KPipes(String applicationId, KPipesConfig config, ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry
        this.pipeBuilder = new PipeBuilder(applicationId, config, serviceRegistry)
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