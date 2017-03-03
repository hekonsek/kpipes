package net.kpipes.lib.testing

import net.kpipes.core.KPipes
import net.kpipes.lib.kafka.broker.TestBroker
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.common.utils.Bytes

class KPipesTest {

    static protected broker = new TestBroker().start()

    static protected kafkaPort = broker.kafkaPort()

    static protected kafkaProducer = new KafkaProducerBuilder<String, Bytes>().port(kafkaPort).build()

    protected KPipes kpipes

}