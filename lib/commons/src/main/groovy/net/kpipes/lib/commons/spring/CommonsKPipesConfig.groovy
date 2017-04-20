package net.kpipes.lib.commons.spring

import net.kpipes.lib.commons.KPipesConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonsKPipesConfig {

    @Bean
    KPipesConfig kpipesConfig(@Value('${applicationId:default}') String applicationId,
                              @Value('${nodeId:default}') String nodeId,
                              @Value('${kafka.host:localhost}') String kafkaHost,
                              @Value('${kafka.port:9092}') int kafkaPort,
                              @Value('${kafka.dataDirectory:kafka_data}') String  kafkaDataDirectory,
                              @Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                              @Value('${zooKeeper.port:2181}') int zooKeeperPort,
                              @Value('${zooKeeper.dataDirectory:zookeeper_data}') String  zooKeeperDataDirectory,
                              @Value('${kpipes.home:/var/kpipes}') File kpipesHome) {
        new KPipesConfig(applicationId, nodeId, kafkaHost, kafkaPort, kafkaDataDirectory, zooKeeperHost, zooKeeperPort, zooKeeperDataDirectory, kpipesHome)
    }


}
