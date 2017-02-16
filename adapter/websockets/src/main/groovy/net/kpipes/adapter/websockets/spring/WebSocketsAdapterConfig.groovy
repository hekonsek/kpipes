package net.kpipes.adapter.websockets.spring

import net.kpipes.adapter.websockets.WebSocketsAdapter
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebSocketsAdapterConfig {

    @Bean(initMethod = 'start')
    WebSocketsAdapter webSocketsAdapter(BrokerAdmin brokerAdmin) {
        new WebSocketsAdapter(new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin), new KafkaProducerBuilder<>().build(), brokerAdmin)
    }

}
