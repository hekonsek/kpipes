package net.kpipes.core.spring

import net.kpipes.core.EventEncoder
import net.kpipes.core.JsonEventEncoder
import net.kpipes.core.KPipesConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringKPipesConfig {

    @Bean
    KPipesConfig kpipesConfig(@Value('${kafka.host:localhost}') String kafkaHost,
                              @Value('${kafka.port:9092}') int kafkaPort,
                              @Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                              @Value('${zooKeeper.port:2181}') int zooKeeperPort) {
        new KPipesConfig(kafkaHost, kafkaPort, zooKeeperHost, zooKeeperPort)
    }

    @ConditionalOnMissingBean
    @Bean
    EventEncoder eventEncoder() {
        new JsonEventEncoder()
    }

}
