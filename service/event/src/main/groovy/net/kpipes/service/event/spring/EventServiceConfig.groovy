package net.kpipes.service.event.spring

import net.kpipes.core.KPipes
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.service.event.EventService
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventServiceConfig {

    @Bean(name = 'event')
    EventService eventService(KPipes kPipes, KafkaProducer kafkaProducer, BrokerAdmin brokerAdmin) {
        new EventService(kPipes, kafkaProducer, brokerAdmin)
    }

}
