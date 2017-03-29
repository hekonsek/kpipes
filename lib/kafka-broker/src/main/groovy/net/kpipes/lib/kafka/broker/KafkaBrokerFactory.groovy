package net.kpipes.lib.kafka.broker

import net.kpipes.lib.commons.KPipesConfig

import static org.slf4j.LoggerFactory.getLogger

class KafkaBrokerFactory {

    private static final LOG = getLogger(KafkaBrokerFactory)

    private final KPipesConfig config

    KafkaBrokerFactory(KPipesConfig config) {
        this.config = config
    }

    // Life-cycle

    KafkaBroker start() {
        if(config.zooKeeperHost == 'localhost') {
            LOG.info('Starting ZooKeeper server.')
            new ZooKeeperServer(config.zooKeeperPort, config.zooKeeperDirectory().absolutePath).start()
        }
        KafkaBroker kafka = null
        while (kafka == null) {
            try {
                kafka = new KafkaBroker(config).start()
            } catch (Exception e) {
                LOG.debug('Failed attempt to start a broker:', e)
                Thread.sleep(100)
            }
        }
        kafka
    }

}