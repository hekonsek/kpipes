package net.kpipes.service.event.spring

import net.kpipes.core.KPipesContext
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.service.event.EventService
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventServiceConfig {

    @Bean(name = 'event')
    EventService eventService(KPipesContext kpipesContext, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {
        new EventService(kpipesContext, kafkaProducer, brokerAdmin)
    }

}
