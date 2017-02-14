package net.kpipes.lib.kafka.broker

import com.google.common.io.Files
import net.kpipes.lib.kafka.broker.KafkaBroker
import net.kpipes.lib.kafka.broker.ZooKeeperServer

class KafkaBrokerFactory {

    KafkaBroker start() {
        def kafkaPort = 9092
        def kafkaData = Files.createTempDir().absolutePath

        def zooKeeperHost = 'localhost'
        def zooKeeperPort = 2181
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