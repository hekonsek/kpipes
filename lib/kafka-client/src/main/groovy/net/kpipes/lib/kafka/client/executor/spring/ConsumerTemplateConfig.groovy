package net.kpipes.lib.kafka.client.executor.spring

import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ConsumerTemplateConfig {

    @Bean
    KafkaConsumerTemplate kafkaConsumerTemplate(BrokerAdmin brokerAdmin) {
        new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin)
    }

}
