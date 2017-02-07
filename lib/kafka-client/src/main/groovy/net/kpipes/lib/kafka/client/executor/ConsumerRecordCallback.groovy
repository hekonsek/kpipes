package net.kpipes.lib.kafka.client.executor

import org.apache.kafka.clients.consumer.ConsumerRecord

interface ConsumerRecordCallback<K,V> {

    void onConsumerRecord(ConsumerRecord<K,V> consumerRecord)

}