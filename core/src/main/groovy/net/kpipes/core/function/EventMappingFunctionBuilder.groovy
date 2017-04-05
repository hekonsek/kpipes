package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipes
import net.kpipes.core.PipeDefinition
import net.kpipes.lib.commons.KPipesConfig
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes

class EventMappingFunctionBuilder implements SimpleFunctionBuilder<EventMappingFunction> {

    private final KPipes kpipes

    EventMappingFunctionBuilder(KPipes kpipes) {
        this.kpipes = kpipes
    }

    @Override
    boolean supports(Object function) {
        function instanceof EventMappingFunction
    }

    @Override
    void build(KPipes kpipes, PipeDefinition pipeDefinition, EventMappingFunction function) {
        kpipes.serviceRegistry().service(BrokerAdmin).ensureTopicExists(pipeDefinition.from(), pipeDefinition.to().get())

        def config = kpipes.serviceRegistry().service(KPipesConfig)

        if(function instanceof FunctionInitializer) {
            function.initialize(kpipes, pipeDefinition)
        }

        def consumerGroup = "event_mapping_function_${config.applicationId()}_${pipeDefinition.id()}"
        def consumer = new KafkaConsumerBuilder(consumerGroup).port(config.kafkaPort()).build()
        kpipes.serviceRegistry().service(KafkaConsumerTemplate).subscribe(consumer, "pipe_${pipeDefinition.id()}", pipeDefinition.effectiveFrom()) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            def result = function.onEvent(new Event(it.topic(), it.key() as String, event, pipeDefinition.functionConfiguration(), kpipes))
            kpipes.serviceRegistry().service(KafkaProducer).send(new ProducerRecord(pipeDefinition.effectiveTo().get(), it.key() as String, new Bytes(new ObjectMapper().writeValueAsBytes(result))))
        }
    }

}