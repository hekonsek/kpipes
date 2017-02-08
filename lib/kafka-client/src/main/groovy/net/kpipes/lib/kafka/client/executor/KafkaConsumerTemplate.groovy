package net.kpipes.lib.kafka.client.executor

import org.apache.kafka.clients.consumer.KafkaConsumer

interface KafkaConsumerTemplate {

    def <K,V> void consumeRecords(KafkaConsumer<K,V> consumer, ConsumerRecordsCallback<K,V> consumerRecordsCallback)

    def <K,V> void consumeRecord(KafkaConsumer<K,V> consumer, ConsumerRecordCallback<K,V> consumerRecordCallback)

    def <K,V> void subscribe(KafkaConsumer<K,V> consumer, String topic, ConsumerRecordCallback<K,V> consumerRecordCallback)

}