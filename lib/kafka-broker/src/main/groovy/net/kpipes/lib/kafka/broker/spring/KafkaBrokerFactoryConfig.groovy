package net.kpipes.lib.kafka.broker.spring

import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.kafka.broker.KafkaBrokerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(name = 'kafka.broker.enabled', havingValue = 'true', matchIfMissing = true)
class KafkaBrokerFactoryConfig {

    @Bean(initMethod = 'start')
    KafkaBrokerFactory kafkaBroker(KPipesConfig config) {
        new KafkaBrokerFactory(config)
    }

}
