package net.kpipes.lib.kafka.broker

import net.kpipes.core.starter.KPipes
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static org.assertj.core.api.Assertions.assertThat

class KafkaBrokerTest {

    @Test
    void shouldReadFromKafka() {
        def kafkaPort = new Random().nextInt(10000) + 1024
        def zooKeeperPort = kafkaPort + 1
        System.setProperty('kafka.port', "${kafkaPort}")
        System.setProperty('kafka.dataDirectory', "${createTempDir().absolutePath}")
        System.setProperty('zooKeeper.port', "${zooKeeperPort}")
        System.setProperty('zooKeeper.dataDirectory', "${createTempDir().absolutePath}")
        new KPipes().start()

        def config = new Properties()
        config.put('acks', 'all')
        config.put('retries', 5)
        config.put("linger.ms", 1);
        config.put('bootstrap.servers', "localhost:${kafkaPort}".toString())
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        def kafkaProducer = new KafkaProducer(config)

        config = new Properties()
        config.put('bootstrap.servers', "localhost:${kafkaPort}".toString())
        config.put('group.id', 'groupName')
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put('enable.auto.commit', "false")
        config.put("auto.offset.reset", "earliest")
        def consumer = new KafkaConsumer<>(config)

        kafkaProducer.send(new ProducerRecord<String, String>('foo', 'key', 'bar'))
        consumer.subscribe(["foo".toString()])
        while(true) {
            def events = consumer.poll(5000).iterator()
            while (events.hasNext()) {
                consumer.commitSync()
                assertThat(events.next().value()).isEqualTo('bar')
                return
            }
            Thread.sleep(100)
        }
    }

}
