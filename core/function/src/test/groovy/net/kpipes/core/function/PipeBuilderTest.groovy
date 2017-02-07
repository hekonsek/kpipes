package net.kpipes.core.function

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.io.Files.createTempDir
import static com.jayway.awaitility.Awaitility.await
import static net.kpipes.lib.commons.Networks.availableTcpPort
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class PipeBuilderTest {

    @Test(timeout = 30000L)
    void pipeShouldInvokeFunction() {
        def kafkaPort = availableTcpPort()
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${availableTcpPort()}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        def kpipes = new KPipes().start()

        def serializer = new EventSerializer()
        new FunctionBinding(kpipes, 'hello.world', { (it.body() as Map).hello = (it.body() as Map).name; it }).start()
        kpipes.service(PipeBuilder).build('source | hello.world | results')

        // When
        def producer = new KafkaProducerBuilder().port(kafkaPort).build()
        producer.send(new ProducerRecord('source', 'key', new Bytes(serializer.serialize(new Event([:], [:], [name: 'henry'])))))

        // Then
        def resultsConsumer = new KafkaConsumerBuilder(uuid()).port(kafkaPort).build()
        await().until({ resultsConsumer.partitionsFor('results').size() > 0 } as Callable<Boolean>)
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

}
