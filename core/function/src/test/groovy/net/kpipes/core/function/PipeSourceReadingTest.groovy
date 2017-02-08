package net.kpipes.core.function

import kafka.admin.AdminUtils
import kafka.admin.RackAwareMode
import kafka.utils.ZKStringSerializer$
import kafka.utils.ZkUtils
import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.pipe.FunctionExecutor
import net.kpipes.core.function.pipe.MockFunctionExecutor
import net.kpipes.core.function.pipe.PipeBuilder
import net.kpipes.core.starter.KPipes
import net.kpipes.core.starter.spi.Service
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.testing.KPipesTest
import org.I0Itec.zkclient.ZkClient
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Test

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static net.kpipes.lib.commons.Uuids.uuid

class PipeSourceReadingTest {

    def source = "source-${uuid()}"

    @Test
    void shouldPassEventToFunctionExecutor() {
        // Given
        def kpipesTest = new KPipesTest()
        kpipesTest.kpipes().serviceRegistry().registerService(new MockFunctionExecutor())
        kpipesTest.start()
        def kpipes = kpipesTest.kpipes()
        def serializer = new EventSerializer()

        def zkClient = new ZkClient('localhost:' + kpipes.configurationResolver().integer('zooKeeper.port', 2181), Integer.MAX_VALUE, 10000, ZKStringSerializer$.MODULE$)
        if (!AdminUtils.topicExists(ZkUtils.apply(zkClient, false), source)) {
            RackAwareMode mode = RackAwareMode.Disabled$.MODULE$
            AdminUtils.createTopic(ZkUtils.apply(zkClient, false), source, 25, 1, new Properties(), mode)
            Thread.sleep(10000)
        }
        kpipes.service(PipeBuilder).build("${source} | function")

        // When
        def producer = new KafkaProducerBuilder().port(kpipesTest.kafkaPort()).build()
        producer.send(new ProducerRecord(source, 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))

        // Then
        def exec = kpipes.service(FunctionExecutor) as MockFunctionExecutor
        await().until({!exec.events().empty} as Callable<Boolean>)
    }

    @Test
    void shouldPassSeriesOfEventsToFunctionExecutor() {
        // Given
        def kpipesTest = new KPipesTest()
        kpipesTest.kpipes().serviceRegistry().registerService(new MockFunctionExecutor())
        kpipesTest.start()
        def kpipes = kpipesTest.kpipes()
        def serializer = new EventSerializer()

        def zkClient = new ZkClient('localhost:' + kpipes.configurationResolver().integer('zooKeeper.port', 2181), Integer.MAX_VALUE, 10000, ZKStringSerializer$.MODULE$)
        if (!AdminUtils.topicExists(ZkUtils.apply(zkClient, false), source)) {
            RackAwareMode mode = RackAwareMode.Disabled$.MODULE$
            AdminUtils.createTopic(ZkUtils.apply(zkClient, false), source, 25, 1, new Properties(), mode)
            Thread.sleep(10000)
        }
        kpipes.service(PipeBuilder).build("${source} | function")

        // When
        def producer = new KafkaProducerBuilder().port(kpipesTest.kafkaPort()).build()
        100.times {
            producer.send(new ProducerRecord(source, 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))
        }

        // Then
        def exec = kpipes.service(FunctionExecutor) as MockFunctionExecutor
        await().until({exec.events().size() == 100} as Callable<Boolean>)
    }

}
