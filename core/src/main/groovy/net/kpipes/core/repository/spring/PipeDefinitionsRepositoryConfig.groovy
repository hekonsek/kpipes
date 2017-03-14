package net.kpipes.core.repository.spring

import net.kpipes.core.KPipesContext
import net.kpipes.core.repository.KafkaPipeDefinitionsRepository
import net.kpipes.core.repository.PipeDefinitionsRepository
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipeDefinitionsRepositoryConfig {

    @Bean
    PipeDefinitionsRepository pipeDefinitionsRepository(KPipesContext kPipesContext, KafkaProducer kafkaProducer) {
        new KafkaPipeDefinitionsRepository(kafkaProducer, kPipesContext)
    }

}
