package net.kpipes.core.function

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.BeforeClass
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static org.assertj.core.api.Assertions.assertThat

class FunctionBindingTest {

    static def kafkaPort = availableTcpPort()

    static KPipes kpipes

    static def producer = new KafkaProducerBuilder().port(kafkaPort).build()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${availableTcpPort()}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        kpipes = new KPipes().start()

        new FunctionBinding(kpipes, 'hello.world', { (it.body() as Map).hello = (it.body() as Map).name; it }).start()
    }

    @Test
    void shouldInvokeFunction() {
        def serializer = new EventSerializer()

        producer.send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results'], [:], [name: 'henry'])))))

        Thread.sleep(5000)
        def resultsConsumer = new KafkaConsumerBuilder('test').port(kafkaPort).build()
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

        producer.send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))
        producer.send(new ProducerRecord('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results2'], [:], [name: 'henry'])))))

        Thread.sleep(5000)
        def resultsConsumer = new KafkaConsumerBuilder('test2').port(kafkaPort).build()
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
