package net.kpipes.core.spring

import net.kpipes.core.RoutingEventFunctionBuilder
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LegacyFunctionBuilderConfig {

    @Bean
    routingEventFunctionBuilder(KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {
        new RoutingEventFunctionBuilder(kafkaProducer, brokerAdmin)
    }

}
