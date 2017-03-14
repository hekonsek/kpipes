package net.kpipes.core

import groovy.transform.CompileStatic
import net.kpipes.core.repository.PipeDefinitionsRepository

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

    void addPipe(PipeDefinition pipeDefinition) {
        serviceRegistry.service(PipeDefinitionsRepository).add(pipeDefinition)
        Thread.sleep(1000)
        serviceRegistry.service(PipeDefinitionsRepository).list().each {
            pipeBuilder.build(it)
        }
        stop()
        start()
    }

    PipeBuilder pipeBuilder() {
        pipeBuilder
    }

    ServiceRegistry serviceRegistry() {
        serviceRegistry
    }

    void stop() {
        pipeBuilder.stop()
    }

}