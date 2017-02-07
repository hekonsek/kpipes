package net.kpipes.core.function

import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

class PipeBuilder {

    private final KPipes kpipes

    PipeBuilder(KPipes kpipes) {
        this.kpipes = kpipes
    }

    void build(String pipeDefinition) {
        def definitionParts = pipeDefinition.split(/\|/).collect{ it.trim() }
        def from = definitionParts[0]

        def functionParts = definitionParts[1].split(' ', 2)
        def functionAddress = functionParts[0]
        def functionConfiguration = functionParts.size() > 1 ? functionParts[1] : null

        def to = definitionParts[2]

        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        def consumer = new KafkaConsumerBuilder<String, Bytes>(pipeDefinition).port(kafkaPort).build()
        consumer.subscribe([from])
        def responseProducer = new KafkaProducerBuilder().port(kafkaPort).build()

        kpipes.service(KafkaConsumerTemplate).consumeRecord(consumer) { eventRecord ->
            def event = new EventSerializer().deserialize(eventRecord.value().get())
            if(functionConfiguration != null) {
                event.metaData().functionConfig = new GroovyShell().evaluate("L:${functionConfiguration}") as Map
            }
            event.metaData().put('target', to)
            responseProducer.send(new ProducerRecord("function.${functionAddress}", eventRecord.key(), new Bytes(new EventSerializer().serialize(event))))
        }
    }

}