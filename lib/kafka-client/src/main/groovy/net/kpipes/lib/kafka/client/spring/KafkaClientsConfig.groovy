package net.kpipes.lib.kafka.client.spring

import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.utils.Bytes
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaClientsConfig {

    @Bean
    KafkaProducer<String, Bytes> kafkaProducer(@Value('${kafka.port:9092}') int kafkaPort) {
        new KafkaProducerBuilder<>().port(kafkaPort).build()
    }

    @Bean
    BrokerAdmin brokerAdmin(@Value('${zooKeeper.host:localhost}') String zooKeeperHost,
                            @Value('${zooKeeper.port:2181}') int zooKeeperPort,
                            @Value('${kafka.defaultPartitionsNumber:1}') int defaultPartitionsNumber) {
        new BrokerAdmin(zooKeeperHost, zooKeeperPort, defaultPartitionsNumber)
    }

}
