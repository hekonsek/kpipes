package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.spi.Function
import net.kpipes.core.starter.KPipes
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

@CompileStatic
class FunctionBinding {

    private final KPipes kPipes

    private final String address

    private final Function function

    FunctionBinding(KPipes kPipes, String address, Function function) {
        this.kPipes = kPipes
        this.address = address
        this.function = function
    }

    void start() {
        def config = new Properties()
        config.put('acks', 'all')
        config.put('retries', 5)
        config.put("linger.ms", 1);
        config.put('bootstrap.servers', 'localhost:9092')
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        config.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer")
        def kafkaProducer = new KafkaProducer(config)

        config = new Properties()
        config.put('bootstrap.servers', 'localhost:9092')
        config.put('group.id', 'function.' + address)
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put("value.deserializer", "org.apache.kafka.common.serialization.BytesDeserializer")
        config.put('enable.auto.commit', "false")
        config.put("auto.offset.reset", "earliest")
        def consumer = new KafkaConsumer<>(config)

        consumer.subscribe(["function.${address}".toString()])
        while(true) {
            def events = consumer.poll(5000).iterator()
            while (events.hasNext()) {
                def record = events.next().value() as Bytes
                def result = function.apply(new EventSerializer().deserialize(record.get()))
                if(result.target().present) {
                    def payload = new Bytes(new ObjectMapper().writeValueAsBytes(result))
                    kafkaProducer.send(new ProducerRecord<String, Bytes>(result.target().get(), result.entityId().orElseGet{ UUID.randomUUID().toString() }, payload))
                }
                consumer.commitSync()
            }
            Thread.sleep(100)
        }
    }

}