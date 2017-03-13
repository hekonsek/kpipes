package net.kpipes.lib.testing

import net.kpipes.core.KPipes
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.broker.TestBroker
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.common.utils.Bytes

import static net.kpipes.lib.commons.Uuids.uuid

class KPipesTest {

    static protected broker = new TestBroker().start()

    static protected kafkaPort = broker.kafkaPort()

    static protected brokerAdmin = new BrokerAdmin('localhost', broker.zooKeeperPort(), 1)

    static protected kafkaProducer = new KafkaProducerBuilder<String, Bytes>().port(kafkaPort).build()

    protected KPipes kpipes

    protected tenant = uuid()

    protected source = uuid()

    protected effectiveSource = "${tenant}.${source}"

    protected target = uuid()

    protected effectiveTarget = "${tenant}.${target}"

}