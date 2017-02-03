package net.kpipes.lib.kafka.client

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.BytesDeserializer
import org.apache.kafka.common.serialization.Serializer

class KafkaConsumerBuilder<K,V> {

    private final String consumerGroup

    private String host = 'localhost'

    private int port = 9092

    private Class<Serializer<V>> valueSerializer = BytesDeserializer

    KafkaConsumerBuilder(String consumerGroup) {
        this.consumerGroup = consumerGroup
    }

    KafkaConsumer<K,V> build(Properties additionalProperties) {
        def config = new Properties()
        config.put('bootstrap.servers', "${host}:${port}" as String)
        config.put('group.id', consumerGroup)
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        config.put('value.deserializer', valueSerializer.name)
        config.put('enable.auto.commit', "false")
        config.put("auto.offset.reset", "earliest")
        config.putAll(additionalProperties)
        new KafkaConsumer<K,V>(config)
    }

    KafkaConsumer<K,V> build() {
        build(new Properties())
    }

    KafkaConsumerBuilder<K,V> host(String host) {
        this.host = host
        this
    }

    KafkaConsumerBuilder<K,V> port(int port) {
        this.port = port
        this
    }

    KafkaConsumerBuilder<K,V> valueSerializer(Class<Serializer<V>> valueSerializer) {
        this.valueSerializer = valueSerializer
        this
    }
}