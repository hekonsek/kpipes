package net.kpipes.lib.kafka.broker

import net.kpipes.lib.commons.KPipesConfig

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid

class TestBroker {

    private final kafkaPort = availableTcpPort()

    private final zooKeeperPort = availableTcpPort()

    TestBroker start() {
        System.setProperty('kafka.broker.enabled', 'false')
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('zooKeeper.port', "${zooKeeperPort}")

        def config = new KPipesConfig(uuid(), uuid(), 'localhost', kafkaPort, 'kafka_data', 'localhost', zooKeeperPort, 'zookeeper_data', createTempDir())
        new KafkaBrokerFactory(config).start()

        this
    }

    int kafkaPort() {
        kafkaPort
    }

    int zooKeeperPort() {
        zooKeeperPort
    }

}