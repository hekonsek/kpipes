package net.kpipes.lib.kafka.client.executor.config

import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.spi.Service
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.executor.CachedThreadPoolKafkaConsumerTemplate
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate

class KafkaConsumerTemplateConfig {

    @Service
    KafkaConsumerTemplate kafkaConsumerTemplate(KPipes kpipes) {
        new CachedThreadPoolKafkaConsumerTemplate(kpipes.service(BrokerAdmin).get())
    }

    @Service
    BrokerAdmin brokerAdmin(KPipes kpipes) {
        def config = kpipes.configurationResolver()
        def zooKeeperHost = config.string('zooKeeper.host', 'localhost')
        def zooKeeperPort = config.integer('zooKeeper.port', 2181)
        new BrokerAdmin(zooKeeperHost, zooKeeperPort)
    }

}
