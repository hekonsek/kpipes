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

class EventRoutingFunctionBuilder implements SimpleFunctionBuilder<EventRoutingFunction> {

    private final KPipes kPipes

    private final KafkaProducer<String, Bytes> kafkaProducer

    private final BrokerAdmin brokerAdmin

    EventRoutingFunctionBuilder(KPipes kPipes, KafkaProducer<String, Bytes> kafkaProducer, BrokerAdmin brokerAdmin) {
        this.kPipes = kPipes
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
    }

    @Override
    boolean supports(Object function) {
        function instanceof EventRoutingFunction
    }

    @Override
    void build(KPipes kpipes, PipeDefinition pipeDefinition, EventRoutingFunction function) {
        kpipes.serviceRegistry().service(BrokerAdmin).ensureTopicExists(pipeDefinition.effectiveFrom())
        if (pipeDefinition.to().present) {
            kpipes.serviceRegistry().service(BrokerAdmin).ensureTopicExists(pipeDefinition.effectiveTo().get())
        }

        def config = kpipes.serviceRegistry().service(KPipesConfig)

        if (function instanceof FunctionInitializer) {
            function.initialize(kpipes, pipeDefinition)
        }

        def consumerGroup = "event_routing_function_${config.applicationId()}_${pipeDefinition.id()}"
        def consumer = new KafkaConsumerBuilder(consumerGroup).port(config.kafkaPort()).build()
        kpipes.serviceRegistry().service(KafkaConsumerTemplate).subscribe(consumer, "pipe_${pipeDefinition.id()}", pipeDefinition.effectiveFrom()) {
            def event = new ObjectMapper().readValue((it.value() as Bytes).get(), Map)
            def destination = function.onEvent(new Event(it.topic(), pipeDefinition.to(), it.key() as String, event, pipeDefinition.functionConfiguration(), kpipes))
            if (destination.present) {
                brokerAdmin.ensureTopicExists(destination.get())
                kpipes.serviceRegistry().service(KafkaProducer).send(new ProducerRecord(destination.get(), it.key() as String, it.value()))
            }
        }
    }

}