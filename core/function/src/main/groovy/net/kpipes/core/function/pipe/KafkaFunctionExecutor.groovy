package net.kpipes.core.function.pipe

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

class KafkaFunctionExecutor implements FunctionExecutor {

    private final KPipes kpipes

    private KafkaProducer<String, Bytes> responseProducer

    KafkaFunctionExecutor(KPipes kpipes) {
        this.kpipes = kpipes
    }

    void start() {
        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        responseProducer = new KafkaProducerBuilder().port(kafkaPort).build()
    }

    @Override
    void executeFunction(String functionAddress, String eventKey, Event event) {
        responseProducer.send(new ProducerRecord(functionAddress, eventKey, new Bytes(new EventSerializer().serialize(event))))
    }

}