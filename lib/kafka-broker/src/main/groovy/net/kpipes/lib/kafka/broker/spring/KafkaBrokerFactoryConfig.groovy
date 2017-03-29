package net.kpipes.lib.kafka.broker.spring

import net.kpipes.lib.kafka.broker.KafkaBrokerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(name = 'kafka.broker.enabled', havingValue = 'true', matchIfMissing = true)
class KafkaBrokerFactoryConfig {

    @Bean(initMethod = 'start')
    KafkaBrokerFactory kafkaBroker(@Value('${kafka.port:9092}') int kafkaPort,
                            @Value('${kafka.dataDirectory:kafka_data}') String  kafkaDataDirectory,
                            @Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                            @Value('${zooKeeper.port:2181}') int zooKeeperPort,
                            @Value('${zooKeeper.dataDirectory:zookeeper_data}') String  zooKeeperDataDirectory) {
        new KafkaBrokerFactory(kafkaPort, kafkaDataDirectory, zooKeeperHost, zooKeeperPort, zooKeeperDataDirectory)
    }

}
