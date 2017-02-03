package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.spi.Function
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static net.kpipes.core.event.EventDto.eventToDto

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

    FunctionBinding start() {
        def kafkaPort = kPipes.configurationResolver().integer('kafka.port', 9092)
        def responseProducer = new KafkaProducerBuilder().port(kafkaPort).build()
        def functionConsumer = new KafkaConsumerBuilder('function.' + address).port(kafkaPort).build()
        functionConsumer.subscribe(["function.${address}" as String])
        kPipes.service(KafkaConsumerTemplate).start(functionConsumer) {
            def events = it.iterator()
            while (events.hasNext()) {
                def record = events.next().value() as Bytes
                def result = function.apply(new EventSerializer().deserialize(record.get()))
                if(result.target().present) {
                    def payload = new Bytes(new ObjectMapper().writeValueAsBytes(eventToDto(result)))
                    responseProducer.send(new ProducerRecord<String, Bytes>(result.target().get(), result.entityId().orElseGet{ UUID.randomUUID().toString() }, payload))
                }
                functionConsumer.commitSync()
            }
        }
        this
    }

}