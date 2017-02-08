package net.kpipes.lib.testing

import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.KafkaProducer

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Networks.availableTcpPort

class KPipesTest {

    private final kpipes = new KPipes()

    private final kafkaPort = availableTcpPort()

    private final zooKeeperPort = availableTcpPort()

    private final eventProducer = new KafkaProducerBuilder().port(kafkaPort).build()

    KPipesTest(Object... servicesToRegister) {
        servicesToRegister.each {
            kpipes.serviceRegistry().registerService(it)
        }
    }

    KPipesTest start() {
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${zooKeeperPort}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        kpipes.start()

        this
    }

    KPipes kpipes() {
        kpipes
    }

    int kafkaPort() {
        kafkaPort
    }

    int zooKeeperPort() {
        zooKeeperPort
    }

    KafkaProducer eventProducer() {
        eventProducer
    }

}
