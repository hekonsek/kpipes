package net.kpipes.adapter.websockets

import io.vertx.core.Vertx
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static io.vertx.core.buffer.Buffer.buffer
import static net.kpipes.lib.commons.Uuids.uuid

class WebSocketsAdapter {

    private final KafkaConsumerTemplate kafkaConsumerTemplate

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    private final int kafkaPort

    WebSocketsAdapter(KafkaConsumerTemplate kafkaConsumerTemplate, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin, int kafkaPort) {
        this.kafkaConsumerTemplate = kafkaConsumerTemplate
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
        this.kafkaPort = kafkaPort
    }

    void start() {
        Vertx.vertx().createHttpServer().websocketHandler { ws ->
            if(ws.uri().startsWith('/event/')) {
                def eventName = ws.uri().replaceFirst(/\/event\//, '')
                brokerAdmin.ensureTopicExists(eventName)
                ws.handler { message ->
                    def key = ws.headers().get('kpipes.key')
                    kafkaProducer.send(new ProducerRecord(eventName, key ?: uuid(), new Bytes(message.bytes)))
                }
            } else if(ws.uri().startsWith('/notification/')) {
                def channelName = ws.uri().replaceFirst(/\/notification\//, '')
                def channel = "notification.${channelName}"
                brokerAdmin.ensureTopicExists(channel) // TODO kafkaConsumerTemplate.subscribe should create topic
                kafkaConsumerTemplate.subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), channel) {
                    ws.write(buffer((it.value() as Bytes).get()))
                }
                ws.closeHandler {
                    // stop kafka consumer
                }
            } else {
                ws.reject()
            }
        }.listen(8080)
    }

}
