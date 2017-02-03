package net.kpipes.lib.kafka.client.executor

import org.apache.kafka.clients.consumer.KafkaConsumer

interface KafkaConsumerTemplate {

    def <K,V> void start(KafkaConsumer<K,V> consumer, ConsumerRecordsCallback<K,V> consumerRecordsCallback)

}