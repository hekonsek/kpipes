package net.kpipes.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.adapter.Tenant
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue

import static net.kpipes.lib.commons.Uuids.uuid
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore

class EventService {

    private final KPipesContext kpipesContext

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    EventService(KPipesContext kpipesContext, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {\
        this.kpipesContext = kpipesContext
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
    }

    void add(@Tenant String tenant, String topic, String key, Map<String, Object> event) {
        if(key == null) {
            key = uuid()
        }
        def effectiveTopic = "${tenant}.${topic}"
        brokerAdmin.ensureTopicExists(effectiveTopic)
        kafkaProducer.send(new ProducerRecord(effectiveTopic, key, new Bytes(new ObjectMapper().writeValueAsBytes(event))))
    }

    Map<String, Map<String, Object>> view(@Tenant String tenant, String topic) {
        def store = kpipesContext.kpipes().pipeBuilder().kafkaStreams().store("${tenant}.${topic}", keyValueStore())
        def results = [:]
        store.all().collect { KeyValue keyValue ->
            results[keyValue.key] = new ObjectMapper().readValue(((Bytes) keyValue.value).get(), Map)
        }
        results
    }

    long count(@Tenant String tenant, String topic) {
        def store = kpipesContext.kpipes().pipeBuilder().kafkaStreams().store("${tenant}.${topic}", keyValueStore())
        store.approximateNumEntries()
    }

}
