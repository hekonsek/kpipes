package net.kpipes.lib.kafka.client.executor.config

import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.spi.Service
import net.kpipes.lib.kafka.client.BrokerAdmin

class BrokerAdminConfig {

    @Service
    BrokerAdmin brokerAdmin(KPipes kpipes) {
        def config = kpipes.configurationResolver()
        def zooKeeperHost = config.string('zooKeeper.host', 'localhost')
        def zooKeeperPort = config.integer('zooKeeper.port', 2181)
        new BrokerAdmin(zooKeeperHost, zooKeeperPort)
    }

}
