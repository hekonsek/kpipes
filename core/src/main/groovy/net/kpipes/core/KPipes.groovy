package net.kpipes.core

import groovy.transform.CompileStatic
import net.kpipes.core.repository.PipeDefinitionsRepository
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate

@CompileStatic
class KPipes {

    private final ServiceRegistry serviceRegistry

    private final PipeBuilder pipeBuilder

    KPipes(ServiceRegistry serviceRegistry, PipeBuilder pipeBuilder) {
        this.serviceRegistry = serviceRegistry
        this.pipeBuilder = pipeBuilder
    }

    KPipes startPipes() {
        pipeBuilder.start()
        this
    }

    KPipes load() {
        Thread.sleep(5000)
        serviceRegistry.service(PipeDefinitionsRepository).list().each {
            pipeBuilder.build(it)
        }
        this
    }

    void addPipe(PipeDefinition pipeDefinition) {
        serviceRegistry.service(PipeDefinitionsRepository).add(pipeDefinition)
        Thread.sleep(1000)
        pipeBuilder.build(pipeDefinition)
        stopPipes()
        startPipes()
    }

    void removePipe(String pipeId) {
        serviceRegistry.service(PipeDefinitionsRepository).remove(pipeId)
        serviceRegistry.service(KafkaConsumerTemplate).stopTask("pipe_${pipeId}")
        Thread.sleep(1000)
        stopPipes()
        startPipes()
    }

    PipeBuilder pipeBuilder() {
        pipeBuilder
    }

    ServiceRegistry serviceRegistry() {
        serviceRegistry
    }

    void stopPipes() {
        pipeBuilder.stop()
    }

    void stop() {
        serviceRegistry.stop()
    }

}