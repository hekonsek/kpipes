package net.kpipes.lib.testing

import net.kpipes.lib.kafka.broker.KafkaBrokerFactory

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Networks.availableTcpPort

class KPipesTest {

    private final kafkaPort = availableTcpPort()

    private final zooKeeperPort = availableTcpPort()

    private final

    KPipesTest start() {
        System.setProperty('kafka.broker.enabled', 'false')

        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${zooKeeperPort}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")

        new KafkaBrokerFactory(kafkaPort, 'localhost', zooKeeperPort).start()

        this
    }

    int kafkaPort() {
        kafkaPort
    }

    int zooKeeperPort() {
        zooKeeperPort
    }


}
