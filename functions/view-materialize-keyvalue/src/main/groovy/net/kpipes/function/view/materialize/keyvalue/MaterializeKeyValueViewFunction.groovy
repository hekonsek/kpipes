package net.kpipes.function.view.materialize.keyvalue

import net.kpipes.core.KPipes
import net.kpipes.core.KPipesConfig
import net.kpipes.core.PipeDefinition
import net.kpipes.core.function.GenericSimpleFunction
import net.kpipes.core.store.FileSystemKeyValueStore
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.common.utils.Bytes

class MaterializeKeyValueViewFunction implements GenericSimpleFunction {

    @Override
    void apply(KPipes kpipes, PipeDefinition pipeDefinition) {
        kpipes.serviceRegistry().service(BrokerAdmin).ensureTopicExists(pipeDefinition.effectiveFrom())
        def applicationId = kpipes.serviceRegistry().service(KPipesConfig).applicationId()
        def nodeId = kpipes.serviceRegistry().service(KPipesConfig).nodeId()
        def kafkaPort = kpipes.serviceRegistry().service(KPipesConfig).kafkaPort()
        def consumer = new KafkaConsumerBuilder("materialized_view_keyvalue_${applicationId}_${nodeId}").port(kafkaPort).build()
        kpipes.serviceRegistry().service(KafkaConsumerTemplate).subscribe(consumer, pipeDefinition.effectiveFrom()) {
            kpipes.serviceRegistry().service(FileSystemKeyValueStore).save(it.topic(), it.key() as String, ((Bytes) it.value()).get())
        }
    }

}