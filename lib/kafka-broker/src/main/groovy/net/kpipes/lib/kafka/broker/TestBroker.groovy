package net.kpipes.lib.kafka.broker

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Networks.availableTcpPort

class TestBroker {

    private final kafkaPort = availableTcpPort()

    private final zooKeeperPort = availableTcpPort()

    TestBroker start() {
        System.setProperty('kafka.broker.enabled', 'false')

        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('zooKeeper.port', "${zooKeeperPort}")

        def kafkaDataDirectory = createTempDir().absolutePath
        def zooKeeperDataDirectory = createTempDir().absolutePath
        new KafkaBrokerFactory(kafkaPort, kafkaDataDirectory, 'localhost', zooKeeperPort, zooKeeperDataDirectory).start()

        this
    }

    int kafkaPort() {
        kafkaPort
    }

    int zooKeeperPort() {
        zooKeeperPort
    }

}