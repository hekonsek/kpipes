package net.kpipes.core.store.spring

import net.kpipes.core.KPipesConfig
import net.kpipes.core.store.FileSystemKeyValueStore
import net.kpipes.core.store.ViewMaterializer
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StoreConfig {

    @Bean
    FileSystemKeyValueStore fileSystemKeyValueStore(KPipesConfig config) {
        def home = config.applicationHome()
        new FileSystemKeyValueStore(new File(home, '/store/fileSystemKeyValue'))
    }

    @Bean
    ViewMaterializer viewMaterializer(BrokerAdmin brokerAdmin, KafkaConsumerTemplate kafkaConsumerTemplate, KPipesConfig kPipesConfig, FileSystemKeyValueStore store) {
        new ViewMaterializer(brokerAdmin, kafkaConsumerTemplate, kPipesConfig, store)
    }

}