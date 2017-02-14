package net.kpipes.lib.kafka.broker.config

import com.google.common.io.Files
import net.kpipes.lib.kafka.broker.KafkaBroker
import net.kpipes.lib.kafka.broker.ZooKeeperServer

class KafkaBrokerConfig {

    KafkaBroker kafkaBroker() {

//        def kafkaPort = config.integer('kafka.port', 9092)
//        def kafkaData = config.string('kafka.dataDirectory', 'kafka_data')
//
//        def zooKeeperHost = config.string('zooKeeper.host', 'localhost')
//        def zooKeeperPort = config.integer('zooKeeper.port', 2181)
//        def zooKeeperData = config.string('zooKeeper.dataDirectory', 'zookeeper_data')

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