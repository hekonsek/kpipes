package net.kpipes.lib.kafka.client

import kafka.admin.AdminUtils
import kafka.admin.RackAwareMode
import kafka.utils.ZKStringSerializer$
import kafka.utils.ZkUtils
import org.I0Itec.zkclient.ZkClient
import org.apache.kafka.common.errors.TopicExistsException
import org.slf4j.Logger

import static org.slf4j.LoggerFactory.getLogger

class BrokerAdmin {

    private static final Logger LOG = getLogger(BrokerAdmin)

    private final String zooKeeperHost

    private final int zooKeeperPort

    BrokerAdmin(String zooKeeperHost, int zooKeeperPort) {
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort

    }

    void ensureTopicExists(String topic) {
        def zkClient = new ZkClient("${zooKeeperHost}:${zooKeeperPort}", Integer.MAX_VALUE, 10000, ZKStringSerializer$.MODULE$)
        ZkUtils zooKeeperUtils = ZkUtils.apply(zkClient, false)

        try {
            if (!AdminUtils.topicExists(zooKeeperUtils, topic)) {
                RackAwareMode mode = RackAwareMode.Disabled$.MODULE$
                AdminUtils.createTopic(zooKeeperUtils, topic, 25, 1, new Properties(), mode)
                Thread.sleep(15000)
            }
        } catch (TopicExistsException e) {
            LOG.debug(e.message)
        }
    }

}
