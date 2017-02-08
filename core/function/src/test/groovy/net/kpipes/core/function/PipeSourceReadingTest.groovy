package net.kpipes.core.function

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.function.pipe.FunctionExecutor
import net.kpipes.core.function.pipe.MockFunctionExecutor
import net.kpipes.core.function.pipe.PipeBuilder
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static net.kpipes.lib.commons.Uuids.uuid

class PipeSourceReadingTest {

    def kpipesTest = new KPipesTest(new MockFunctionExecutor()).start()

    def kpipes = kpipesTest.kpipes()

    def source = "source-${uuid()}"

    @Before
    void before() {
        kpipes.service(BrokerAdmin).ensureTopicExists(source)
    }

    @Test
    void shouldPassEventToFunctionExecutor() {
        // Given
        def serializer = new EventSerializer()
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
        def serializer = new EventSerializer()
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
