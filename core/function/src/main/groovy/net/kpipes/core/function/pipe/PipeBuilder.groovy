package net.kpipes.core.function.pipe

import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.common.utils.Bytes

class PipeBuilder {

    private final KPipes kpipes

    PipeBuilder(KPipes kpipes) {
        this.kpipes = kpipes
    }

    // Operations

    void build(String pipeDefinition) {
        def definitionParts = pipeDefinition.split(/\|/).collect{ it.trim() }
        def from = definitionParts[0]

        def functionParts = definitionParts[1].split(' ', 2)
        def functionAddress = functionParts[0]
        def functionConfiguration = functionParts.size() > 1 ? functionParts[1] : null

        kpipes.service(BrokerAdmin).get().ensureTopicExists(functionAddress)

        def to = definitionParts[2]
        if(to != null) {
            kpipes.service(BrokerAdmin).get().ensureTopicExists(to)
        }

        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        def functionExecutor = kpipes.service(FunctionExecutor).get()
        def consumer = new KafkaConsumerBuilder<String, Bytes>(pipeDefinition).port(kafkaPort).build()
        kpipes.service(KafkaConsumerTemplate).get().subscribe(consumer, from) { eventRecord ->
            def event = new EventSerializer().deserialize(eventRecord.value().get())
            if(functionConfiguration != null) {
                event.metaData().functionConfig = new GroovyShell().evaluate("L:${functionConfiguration}") as Map
            }
            event.metaData().put('target', to)
            functionExecutor.executeFunction("function.${functionAddress}", eventRecord.key(), event)
        }
    }

}