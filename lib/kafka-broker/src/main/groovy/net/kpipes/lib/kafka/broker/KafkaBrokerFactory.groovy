package net.kpipes.lib.kafka.broker

import com.google.common.io.Files
import net.kpipes.lib.kafka.broker.KafkaBroker
import net.kpipes.lib.kafka.broker.ZooKeeperServer

class KafkaBrokerFactory {

    int kafkaPort

    String zooKeeperHost

    int zooKeeperPort

    KafkaBrokerFactory(int kafkaPort, String zooKeeperHost, int zooKeeperPort) {
        this.kafkaPort = kafkaPort
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort
    }

    KafkaBroker start() {
        def kafkaData = Files.createTempDir().absolutePath
        def zooKeeperData = Files.createTempDir().absolutePath

        new ZooKeeperServer(zooKeeperPort, kafkaData).start()
        KafkaBroker kafka = null
        while (kafka == null) {
            try {
                kafka = new KafkaBroker(kafkaPort, zooKeeperHost, zooKeeperPort, zooKeeperData).start()
            } catch (Exception e) {
                e.printStackTrace()
                Thread.sleep(100)
            }
        }
        kafka
    }

}