package net.kpipes.lib.kafka.client.executor

import org.apache.kafka.clients.consumer.ConsumerRecords

interface ConsumerRecordsCallback<K,V> {

    void onConsumerRecords(ConsumerRecords<K,V> consumerRecords)

}