package net.kpipes.core.store

import net.kpipes.core.KPipesConfig
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.common.utils.Bytes

class ViewMaterializer {

    private final BrokerAdmin brokerAdmin

    private final KafkaConsumerTemplate consumerTemplate

    private final KPipesConfig config

    private final FileSystemKeyValueStore store

    ViewMaterializer(BrokerAdmin brokerAdmin, KafkaConsumerTemplate consumerTemplate, KPipesConfig config, FileSystemKeyValueStore store) {
        this.brokerAdmin = brokerAdmin
        this.consumerTemplate = consumerTemplate
        this.config = config
        this.store = store
    }

    void materialize(String topic) {
        brokerAdmin.ensureTopicExists(topic)
        def consumer = new KafkaConsumerBuilder("materialized_view_keyvalue_${config.applicationId()}_${config.nodeId()}").port(config.kafkaPort()).build()
        consumerTemplate.subscribe(consumer, topic) {
            store.save(it.topic(), it.key() as String, ((Bytes) it.value()).get())
        }
    }

}
