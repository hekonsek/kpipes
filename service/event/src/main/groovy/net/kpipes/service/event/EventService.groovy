package net.kpipes.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.KPipesContext
import net.kpipes.core.adapter.Tenant
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.commons.lang3.Validate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreType
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.Logger

import static net.kpipes.lib.commons.Uuids.uuid
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore
import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
class EventService {

    private final static Logger LOG = getLogger(EventService)

    private final KPipesContext kpipesContext

    private final KafkaProducer kafkaProducer

    private final BrokerAdmin brokerAdmin

    EventService(KPipesContext kpipesContext, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {\
        this.kpipesContext = kpipesContext
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
        store(tenant, topic).all().collect { KeyValue keyValue ->
            results[keyValue.key] = new ObjectMapper().readValue(((Bytes) keyValue.value).get(), Map)
        }
        results
    }

    long count(@Tenant String tenant, String topic) {
        store(tenant, topic).approximateNumEntries()
    }

    // Helpers

    ReadOnlyKeyValueStore<String, Bytes> store(String tenant, String topic) {
        def storeName = "${tenant}.${topic}"
        LOG.debug('Retrieving key/value store: {}', storeName)
        def store = kpipesContext.kpipes().pipeBuilder().kafkaStreams().store(storeName, keyValueStore())
        Validate.notNull(store, "Store %s doesn't exist. Are you sure you have created materialized key/value view with this name?", storeName)
        store
    }

}
