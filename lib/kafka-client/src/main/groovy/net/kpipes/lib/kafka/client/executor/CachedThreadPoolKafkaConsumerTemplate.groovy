package net.kpipes.lib.kafka.client.executor

import groovy.transform.CompileStatic
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException

import java.util.concurrent.ExecutorService

import static java.util.concurrent.Executors.newCachedThreadPool

@CompileStatic
class CachedThreadPoolKafkaConsumerTemplate implements KafkaConsumerTemplate {

    private final BrokerAdmin brokerAdmin

    private final ExecutorService executor = newCachedThreadPool()

    CachedThreadPoolKafkaConsumerTemplate(BrokerAdmin brokerAdmin) {
        this.brokerAdmin = brokerAdmin
    }

    @Override
    def <K, V> void consumeRecords(KafkaConsumer<K, V> consumer, ConsumerRecordsCallback<K, V> consumerRecordsCallback) {
        executor.submit {
            boolean isRunning = true
            while (isRunning) {
                try {
                    def events = consumer.poll(5000)
                    consumerRecordsCallback.onConsumerRecords(events)
                    Thread.sleep(100)
                } catch (WakeupException e) {
                    isRunning = false
                    consumer.close()
                }
            }
        }
    }

    @Override
    def <K, V> void consumeRecord(KafkaConsumer<K, V> consumer, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        consumeRecords(consumer) { records ->
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
    def <K, V> void subscribe(KafkaConsumer<K, V> consumer, String topic, ConsumerRecordCallback<K, V> consumerRecordCallback) {
        brokerAdmin.ensureTopicExists(topic)
        consumer.subscribe([topic])
        consumeRecord(consumer, consumerRecordCallback)
    }

}