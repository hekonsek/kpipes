package net.kpipes.core.repository.spring

import net.kpipes.core.Json
import net.kpipes.core.repository.KafkaPipeDefinitionsRepository
import net.kpipes.core.repository.PipeDefinitionsRepository
import net.kpipes.core.store.FileSystemKeyValueStore
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipeDefinitionsRepositoryConfig {

    @Bean
    PipeDefinitionsRepository pipeDefinitionsRepository(KafkaProducer kafkaProducer, FileSystemKeyValueStore store, Json json) {
        new KafkaPipeDefinitionsRepository(kafkaProducer, store, json)
    }

}
