package net.kpipes.executor

import net.kpipes.core.Json
import net.kpipes.core.function.Event
import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

import java.util.regex.Pattern

class ServiceEventPipeline {

    private final KPipesConfig config

    private final Json json

    private final KafkaConsumerTemplate consumer

    private final KafkaProducer producer

    private final List<FunctionExecutor> functionExecutors

    ServiceEventPipeline(KPipesConfig config, Json json, KafkaConsumerTemplate consumer, KafkaProducer producer, List<FunctionExecutor> functionExecutors) {
        this.config = config
        this.json = json
        this.consumer = consumer
        this.producer = producer
        this.functionExecutors = functionExecutors
    }

    void start() {
        def kafkaConsumer = new KafkaConsumerBuilder('kpipes-executor-service-pipeline').port(config.kafkaPort()).build()
        consumer.subscribe(kafkaConsumer, 'kpipes-executor-service-pipeline', Pattern.compile(/.+?\.service\.request\..+/)) {
            def serviceRequest = json.read(it.value() as Bytes)
            def config = [service: serviceRequest.service, operation: serviceRequest.operation]
            def event = new Event(it.topic(), it.key() as String, serviceRequest.body as Map, config, null)
            def executor = functionExecutors.find{ it.supports(event) }
            def outputEvent = executor.onEvent(event)
            producer.send(new ProducerRecord(it.topic().replaceFirst('request', 'response'), it.key(), json.asBytes(outputEvent)))
        }
    }

}
