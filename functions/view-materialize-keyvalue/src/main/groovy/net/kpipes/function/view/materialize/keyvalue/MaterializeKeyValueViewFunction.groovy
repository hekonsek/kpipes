package net.kpipes.function.view.materialize.keyvalue

import net.kpipes.core.KPipesContext
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import net.kpipes.core.function.GenericSimpleFunction
import net.kpipes.core.store.FileSystemKeyValueStore
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.common.utils.Bytes

class MaterializeKeyValueViewFunction implements GenericSimpleFunction {

    @Override
    void apply(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition) {
        pipeBuilder.@serviceRegistry.service(BrokerAdmin).ensureTopicExists(pipeDefinition.effectiveFrom())
        def applicationId = pipeBuilder.@serviceRegistry.service(KPipesContext).applicationId()
        def nodeId = pipeBuilder.@serviceRegistry.service(KPipesContext).nodeId()
        def kafkaPort = pipeBuilder.@serviceRegistry.service(KPipesContext).kafkaPort()
        def consumer = new KafkaConsumerBuilder("materialized_view_keyvalue_${applicationId}_${nodeId}").port(kafkaPort).build()
        pipeBuilder.@serviceRegistry.service(KafkaConsumerTemplate).subscribe(consumer, pipeDefinition.effectiveFrom()) {
            pipeBuilder.@serviceRegistry.service(FileSystemKeyValueStore).save(it.topic(), it.key() as String, ((Bytes) it.value()).get())
        }
    }

}