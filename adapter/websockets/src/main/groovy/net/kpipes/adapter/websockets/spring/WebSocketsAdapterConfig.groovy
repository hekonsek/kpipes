package net.kpipes.adapter.websockets.spring

import net.kpipes.adapter.websockets.AnonymousAuthenticator
import net.kpipes.adapter.websockets.WebSocketsAdapter
import net.kpipes.core.KPipes
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import net.kpipes.adapter.websockets.Authenticator as KPipesAuthenticator

@Configuration
class WebSocketsAdapterConfig {

    @Bean(initMethod = 'start')
    WebSocketsAdapter webSocketsAdapter(KPipes kPipes, BrokerAdmin brokerAdmin, KPipesAuthenticator authenticator,
                                        @Value('${http.port:8080}') int httpPort,
                                        @Value('${kafka.port:9092}') int kafkaPort) {
        new WebSocketsAdapter(kPipes, new CachedThreadPoolKafkaConsumerTemplate(brokerAdmin), new KafkaProducerBuilder<>().port(kafkaPort).build(), brokerAdmin, authenticator, httpPort, kafkaPort)
    }

    @Bean
    @ConditionalOnMissingBean
    KPipesAuthenticator authenticator() {
        new AnonymousAuthenticator()
    }

}
