package net.kpipes.core.function

import net.kpipes.core.event.Event
import net.kpipes.core.event.EventSerializer
import net.kpipes.core.starter.KPipes
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static org.assertj.core.api.Assertions.assertThat

class FunctionBindingTest {

    @Test
    void shouldInvokeFunction() {
        def kafkaPort = new Random().nextInt(10000) + 1024
        def zooKeeperPort = kafkaPort + 1
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${zooKeeperPort}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        def kpipes = new KPipes().start()
        def serializer = new EventSerializer()
        new FunctionBinding(kpipes, 'hello.world', { (it.body() as Map).hello = (it.body() as Map).name; it }).start()

        def config = new Properties()
        config.put('acks', 'all')
        config.put('retries', 5)
        config.put("linger.ms", 1);
        config.put('bootstrap.servers', "localhost:${kafkaPort}".toString())
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        config.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer")
        def kafkaProducer = new KafkaProducer(config)

        config = new Properties()
        config.put('bootstrap.servers', "localhost:${kafkaPort}".toString())
        config.put('group.id', 'groupName')
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put("value.deserializer", "org.apache.kafka.common.serialization.BytesDeserializer")
        config.put('enable.auto.commit', "false")
        config.put("auto.offset.reset", "earliest")
        def consumer = new KafkaConsumer<>(config)

        kafkaProducer.send(new ProducerRecord<String, Bytes>('function.hello.world', 'key', new Bytes(serializer.serialize(new Event([target: 'results'], [:], [name: 'henry'])))))
        consumer.subscribe(["results".toString()])
        while(true) {
            def events = consumer.poll(5000).iterator()
            while (events.hasNext()) {
                consumer.commitSync()
                def bytes = events.next().value() as Bytes
                def event = serializer.deserialize(bytes.get())
                assertThat((event.body() as Map).hello).isEqualTo('henry')
                return
            }
            Thread.sleep(100)
        }
    }

}
