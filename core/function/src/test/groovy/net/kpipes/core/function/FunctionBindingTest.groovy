package net.kpipes.core.function

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.BeforeClass
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class FunctionBindingTest {

    static def kpipesTest = new KPipesTest().start()

    @BeforeClass
    static void beforeClass() {
        new FunctionBinding(kpipesTest.kpipes(), 'hello.world', { (it.body() as Map).hello = (it.body() as Map).name; it }).start()
    }

    @Test
    void shouldInvokeFunction() {
        def serializer = new EventSerializer()

        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results'], [:], [name: 'henry'])))))

        Thread.sleep(5000)
        def resultsConsumer = new KafkaConsumerBuilder('test').port(kpipesTest.kafkaPort()).build()
        resultsConsumer.subscribe(['results'])
        while(true) {
            def events = resultsConsumer.poll(5000).iterator()
            while (events.hasNext()) {
                resultsConsumer.commitSync()
                def bytes = events.next().value() as Bytes
                def event = serializer.deserialize(bytes.get())
                assertThat((event.body() as Map).hello).isEqualTo('henry')
                return
            }
            Thread.sleep(100)
        }
    }

    @Test
    void shouldInvokeFunctionTwice() {
        def serializer = new EventSerializer()

        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))
        kpipesTest.eventProducer().send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))

        Thread.sleep(5000)
        def resultsConsumer = new KafkaConsumerBuilder('test2').port(kpipesTest.kafkaPort()).build()
        resultsConsumer.subscribe(['results2'])
        def eventsCount = 0
        while(true) {
            def events = resultsConsumer.poll(5000).iterator()
            while (events.hasNext()) {
                eventsCount++
                if(eventsCount == 2) {
                    return
                }
                resultsConsumer.commitSync()
            }
            Thread.sleep(100)
        }
    }

}
