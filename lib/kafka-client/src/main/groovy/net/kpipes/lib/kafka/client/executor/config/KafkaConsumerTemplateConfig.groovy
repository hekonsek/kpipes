package net.kpipes.lib.kafka.client.executor.config

import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.Service
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate

class KafkaConsumerTemplateConfig {

    @Service
    KafkaConsumerTemplate kafkaConsumerTemplate(KPipes kpipes) {
        new CachedThreadPoolKafkaConsumerTemplate()
    }

}
