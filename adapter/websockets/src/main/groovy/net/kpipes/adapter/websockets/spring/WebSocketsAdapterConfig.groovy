package net.kpipes.adapter.websockets.spring

import net.kpipes.adapter.websockets.WebSocketsAdapter
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebSocketsAdapterConfig {

    @Bean(initMethod = 'start')
    WebSocketsAdapter webSocketsAdapter(@Value('${kafka.port:9092}') int kafkaPort, BrokerAdmin brokerAdmin) {
        new WebSocketsAdapter(new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin), new KafkaProducerBuilder<>().port(kafkaPort).build(), brokerAdmin, kafkaPort)
    }

}
