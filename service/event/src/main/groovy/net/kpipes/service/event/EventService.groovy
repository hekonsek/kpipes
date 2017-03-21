package net.kpipes.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.adapter.Tenant
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import static net.kpipes.lib.commons.Uuids.uuid

class EventService {

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    EventService(KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
    }

    void add(@Tenant String tenant, String topic, String key, Map<String, Object> event) {
        if(topic == null) {
            topic = uuid()
        }
        def effectiveTopic = "${tenant}.${topic}"
        brokerAdmin.ensureTopicExists(effectiveTopic)
        kafkaProducer.send(new ProducerRecord(effectiveTopic, key, new Bytes(new ObjectMapper().writeValueAsBytes(event))))
    }

}
