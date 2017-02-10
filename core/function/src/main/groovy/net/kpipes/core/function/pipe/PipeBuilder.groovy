package net.kpipes.core.function.pipe

import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.common.utils.Bytes

import static net.kpipes.core.function.pipe.PipeDefinition.parsePipeDefinition

class PipeBuilder {

    private final KPipes kpipes

    private FunctionExecutor functionExecutor

    PipeBuilder(KPipes kpipes) {
        this.kpipes = kpipes
    }

    void start() {
        this.functionExecutor = kpipes.service(FunctionExecutor).get()
    }

    // Operations

    void build(String pipeDefinition) {
        build(parsePipeDefinition(pipeDefinition))
    }

    void build(PipeDefinition pipeDefinition) {
        kpipes.service(BrokerAdmin).get().ensureTopicExists(pipeDefinition.from())
        kpipes.service(BrokerAdmin).get().ensureTopicExists(pipeDefinition.functionAddress())
        if (pipeDefinition.to().present) {
            kpipes.service(BrokerAdmin).get().ensureTopicExists(pipeDefinition.to().get())
        }

        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        def consumer = new KafkaConsumerBuilder<String, Bytes>(pipeDefinition.id()).port(kafkaPort).build()
        kpipes.service(KafkaConsumerTemplate).get().subscribe(consumer, pipeDefinition.from()) { eventRecord ->
            def event = new EventSerializer().deserialize(eventRecord.value().get())
            if (!pipeDefinition.functionConfiguration().isEmpty()) {
                event.metaData().functionConfig = pipeDefinition.functionConfiguration()
            }
            if (pipeDefinition.to().present) {
                event.metaData().put('target', pipeDefinition.to().get())
            }
            functionExecutor.executeFunction("function.${pipeDefinition.functionAddress()}", eventRecord.key(), event)
        }
    }

}