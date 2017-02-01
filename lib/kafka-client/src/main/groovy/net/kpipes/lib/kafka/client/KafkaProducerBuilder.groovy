package net.kpipes.lib.kafka.client

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.BytesSerializer
import org.apache.kafka.common.serialization.Serializer

class KafkaProducerBuilder<K,V> {

    private String host = 'localhost'

    private int port = 9092

    private int retries = 5

    private Class<Serializer<V>> valueSerializer = BytesSerializer

    KafkaProducer<K,V> build(Properties additionalProperties) {
        def config = new Properties()
        config.put('bootstrap.servers', "${host}:${port}" as String)
        config.put('retries', retries)
        config.put('acks', 'all')
        config.put("linger.ms", 1);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        config.put('value.serializer', valueSerializer.name)
        config.putAll(additionalProperties)
        new KafkaProducer<K,V>(config)
    }

    KafkaProducer<K,V> build() {
        build(new Properties())
    }

    KafkaProducerBuilder<K,V> host(String host) {
        this.host = host
        this
    }

    KafkaProducerBuilder<K,V> port(int port) {
        this.port = port
        this
    }

    KafkaProducerBuilder<K,V> retries(int retries) {
        this.retries = retries
        this
    }

    KafkaProducerBuilder<K,V> valueSerializer(Class<Serializer<V>> valueSerializer) {
        this.valueSerializer = valueSerializer
        this
    }
}