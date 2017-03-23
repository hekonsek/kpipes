package net.kpipes.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipes
import net.kpipes.core.adapter.Tenant
import net.kpipes.core.store.FileSystemKeyValueStore
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.slf4j.Logger

import static net.kpipes.lib.commons.Uuids.uuid
import static org.slf4j.LoggerFactory.getLogger

class EventService {

    private final static Logger LOG = getLogger(EventService)

    private final KPipes kpipes

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    EventService(KPipes kpipes, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {
        this.kpipes = kpipes
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
    }

    // Operations

    void add(@Tenant String tenant, String topic, String key, Map<String, Object> event) {
        if(key == null) {
            key = uuid()
        }
        def effectiveTopic = "${tenant}.${topic}"
        brokerAdmin.ensureTopicExists(effectiveTopic)
        kafkaProducer.send(new ProducerRecord(effectiveTopic, key, new Bytes(new ObjectMapper().writeValueAsBytes(event))))
    }

    Map<String, Map<String, Object>> view(@Tenant String tenant, String topic) {
        def results = [:]
        kpipes.serviceRegistry().service(FileSystemKeyValueStore).all("${tenant}.${topic}").each {
            results[it.key] = new ObjectMapper().readValue(it.value, Map)
        }
        results
    }

    long count(@Tenant String tenant, String topic) {
        kpipes.serviceRegistry().service(FileSystemKeyValueStore).count("${tenant}.${topic}")
    }

}
