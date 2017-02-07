package net.kpipes.core.function

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class FunctionBindingTest {

    static def kpipesTest = new KPipesTest().start()

    @BeforeClass
    static void beforeClass() {
        new FunctionBinding(kpipesTest.kpipes(), 'hello.world', {
            (it.body() as Map).hello = (it.body() as Map).name; it
        }).start()
    }

    @Test(timeout = 30000L)
    void shouldInvokeFunction(TestContext context) {
        def async = context.async()
        def serializer = new EventSerializer()

        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results'], [:], [name: 'henry'])))))

        def resultsConsumer = new KafkaConsumerBuilder<String, Bytes>('test').port(kpipesTest.kafkaPort()).build()
        await().until({ resultsConsumer.partitionsFor('results').size() > 0 } as Callable<Boolean>)
        resultsConsumer.subscribe(['results'])
        kpipesTest.kpipes().service(KafkaConsumerTemplate).consumeRecord(resultsConsumer) {
            def event = serializer.deserialize(it.value().get())
            assertThat(event.body().hello).isEqualTo('henry')
            async.complete()
        }
    }

    @Test
    void shouldInvokeFunctionTwice() {
        def serializer = new EventSerializer()

        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))
        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))

        def resultsConsumer = new KafkaConsumerBuilder('test2').port(kpipesTest.kafkaPort()).build()
        await().until({ resultsConsumer.partitionsFor('results2').size() > 0 } as Callable<Boolean>)
        resultsConsumer.subscribe(['results2'])
        def eventsCount = 0
        while (true) {
            def events = resultsConsumer.poll(5000).iterator()
            while (events.hasNext()) {
                eventsCount++
                if (eventsCount == 2) {
                    return
                }
                resultsConsumer.commitSync()
            }
            Thread.sleep(100)
        }
    }

}
