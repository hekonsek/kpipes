package net.kpipes.core.spring

import net.kpipes.core.KPipesConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringKPipesConfig {

    @Bean
    KPipesConfig kPipesConfig(@Value('${kafka.host:localhost}') String kafkaHost,
                              @Value('${kafka.port:9092}') int kafkaPort,
                              @Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                              @Value('${zooKeeper.port:2181}') int zooKeeperPort) {
        new KPipesConfig(kafkaHost, kafkaPort, zooKeeperHost, zooKeeperPort)
    }

}
