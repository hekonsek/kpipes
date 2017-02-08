package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.spi.Function
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static net.kpipes.core.event.EventDto.eventToDto

@CompileStatic
class FunctionBinding {

    private final KPipes kpipes

    private final String address

    private final Function function

    FunctionBinding(KPipes kpipes, String address, Function function) {
        this.kpipes = kpipes
        this.address = address
        this.function = function
    }

    static FunctionBinding functionBinding(KPipes kpipes, String address, Function function) {
        new FunctionBinding(kpipes, address, function)
    }

    FunctionBinding start() {
        def kafkaPort = kpipes.configurationResolver().integer('kafka.port', 9092)
        def responseProducer = new KafkaProducerBuilder().port(kafkaPort).build()
        def functionConsumer = new KafkaConsumerBuilder<String, Bytes>('function.' + address).port(kafkaPort).build()
        kpipes.service(KafkaConsumerTemplate).get().subscribe(functionConsumer, "function.${address}") {
            def result = function.apply(new EventSerializer().deserialize(it.value().get()))
            if (result.target().present) {
                def payload = new Bytes(new ObjectMapper().writeValueAsBytes(eventToDto(result)))
                responseProducer.send(new ProducerRecord<String, Bytes>(result.target().get(), result.entityId().orElseGet {
                    UUID.randomUUID().toString()
                }, payload))
            }
        }
        this
    }

}