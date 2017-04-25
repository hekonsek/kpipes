package net.kpipes.lib.kafka.client.executor

import groovy.transform.CompileStatic
import net.kpipes.lib.commons.Uuids
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

import static java.util.concurrent.Executors.newCachedThreadPool
import static net.kpipes.lib.commons.Uuids.uuid

@CompileStatic
class CachedThreadPoolKafkaConsumerTemplate implements KafkaConsumerTemplate {

    private final BrokerAdmin brokerAdmin

    private final ExecutorService executor = newCachedThreadPool()

    private final Map<String, AtomicBoolean> stopRequests = [:]

    CachedThreadPoolKafkaConsumerTemplate(BrokerAdmin brokerAdmin) {
        this.brokerAdmin = brokerAdmin
    }

    @Override
    def <K, V> void consumeRecords(KafkaConsumer<K, V> consumer, String taskId, ConsumerRecordsCallback<K, V> consumerRecordsCallback) {
        stopRequests.put(taskId, new AtomicBoolean(false))
        executor.submit {
            while (!stopRequests[taskId].get()) {
                try {
                    def events = consumer.poll(5000)
                    consumerRecordsCallback.onConsumerRecords(events)
                    Thread.sleep(100)
                } catch (WakeupException e) {
                    stopRequests[taskId].set(true)
                    consumer.close()
                }
            }
        }
    }

    @Override
    def <K, V> void consumeRecord(KafkaConsumer<K, V> consumer, String taskId, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        consumeRecords(consumer, taskId) { ConsumerRecords<K, V> records ->
            def iterator = records.iterator()
            while (iterator.hasNext()) {
                def record = iterator.next()
                consumerRecordCallback.onConsumerRecord(record)
                consumer.commitSync()
            }
            Thread.sleep(100)
        }
    }

    @Override
    def <K, V> void subscribe(KafkaConsumer<K, V> consumer, String taskId, String topic, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        brokerAdmin.ensureTopicExists(topic)
        consumer.subscribe([topic])
        consumeRecord(consumer, taskId, consumerRecordCallback)
    }

    @Override
    def <K, V> void subscribe(KafkaConsumer<K, V> consumer, String taskId, Pattern topics, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        consumer.subscribe(topics, new ConsumerRebalanceListener() {
            @Override
            void onPartitionsRevoked(Collection<TopicPartition> partitions) {

            }

            @Override
            void onPartitionsAssigned(Collection<TopicPartition> partitions) {
//                partitions.each {
//                    brokerAdmin.ensureTopicExists(it.topic())
//                }
            }
        })
        consumeRecord(consumer, taskId, consumerRecordCallback)
    }

    @Override
    def <K, V> void subscribe(KafkaConsumer<K, V> consumer, String topic, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        subscribe(consumer, uuid(), topic, consumerRecordCallback)
    }

    @Override
    def <K, V> void subscribe(KafkaConsumer<K, V> consumer, Pattern topics, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        subscribe(consumer, uuid(), topics, consumerRecordCallback)
    }

    @Override
    void stopTask(String taskId) {
        if(stopRequests.containsKey(taskId)) {
            stopRequests[taskId].set(true)
        }
    }

}