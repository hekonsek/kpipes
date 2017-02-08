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

import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class FunctionBindingTest {

    static def kpipesTest = new KPipesTest().start()

    @BeforeClass
    static void beforeClass() {
        new FunctionBinding(kpipesTest.kpipes(), 'hello.world', {
            it.body().hello = it.body().name; it
        }).start()
    }

    @Test(timeout = 30000L)
    void shouldInvokeFunction(TestContext context) {
        def async = context.async()
        def serializer = new EventSerializer()

        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results'], [:], [name: 'henry'])))))

        def resultsConsumer = new KafkaConsumerBuilder<String, Bytes>(uuid()).port(kpipesTest.kafkaPort()).build()
        kpipesTest.kpipes().service(KafkaConsumerTemplate).subscribe(resultsConsumer, 'results') {
            def event = serializer.deserialize(it.value().get())
            assertThat(event.body().hello).isEqualTo('henry')
            async.complete()
        }
    }

    @Test(timeout = 30000L)
    void shouldInvokeFunctionTwice(TestContext context) {
        def async = context.async()
        def serializer = new EventSerializer()

        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))
        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))

        def resultsConsumer = new KafkaConsumerBuilder(uuid()).port(kpipesTest.kafkaPort()).build()
        def eventsCount = 0
        kpipesTest.kpipes().service(KafkaConsumerTemplate).subscribe(resultsConsumer, 'results2') {
            eventsCount++
            if(eventsCount == 2) {
                async.complete()
            }
        }
    }

}
