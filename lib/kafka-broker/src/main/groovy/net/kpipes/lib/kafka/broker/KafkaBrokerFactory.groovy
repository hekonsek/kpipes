package net.kpipes.lib.kafka.broker

import com.google.common.io.Files

import static org.slf4j.LoggerFactory.getLogger

class KafkaBrokerFactory {

    private static final LOG = getLogger(KafkaBrokerFactory)

    private final int kafkaPort

    private final String kafkaDataDirectory

    private final String zooKeeperHost

    private final int zooKeeperPort

    private final String zooKeeperDataDirectory

    KafkaBrokerFactory(int kafkaPort, String kafkaDataDirectory, String zooKeeperHost, int zooKeeperPort, String zooKeeperDataDirectory) {
        this.kafkaPort = kafkaPort
        this.kafkaDataDirectory = kafkaDataDirectory
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort
        this.zooKeeperDataDirectory = zooKeeperDataDirectory
    }

    // Life-cycle

    KafkaBroker start() {
        new ZooKeeperServer(zooKeeperPort, kafkaDataDirectory).start()
        KafkaBroker kafka = null
        while (kafka == null) {
            try {
                kafka = new KafkaBroker(kafkaPort, zooKeeperHost, zooKeeperPort, zooKeeperDataDirectory).start()
            } catch (Exception e) {
                LOG.debug('Failed attempt to start a broker:', e)
                Thread.sleep(100)
            }
        }
        kafka
    }

}