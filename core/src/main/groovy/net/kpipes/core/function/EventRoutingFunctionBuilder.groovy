package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import net.kpipes.lib.kafka.client.BrokerAdmin
import net.kpipes.lib.kafka.client.KafkaProducerBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.errors.TopologyBuilderException
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.processor.AbstractProcessor
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.processor.TopologyBuilder

class EventRoutingFunctionBuilder implements TopologyFunctionBuilder<EventRoutingFunction> {

    private final KPipesContext kPipesContext

    private final KafkaProducer<String, Bytes> kafkaProducer

    private final BrokerAdmin brokerAdmin

    EventRoutingFunctionBuilder(KPipesContext kPipesContext, KafkaProducer<String, Bytes> kafkaProducer, BrokerAdmin brokerAdmin) {
        this.kPipesContext = kPipesContext
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
    }

    @Override
    boolean supports(Object function) {
        function instanceof EventRoutingFunction
    }

    @Override
    void build(PipeBuilder pipeBuilder, TopologyBuilder topologyBuilder, PipeDefinition pipeDefinition, EventRoutingFunction function) {
        if (function instanceof FunctionInitializer) {
            function.initialize(pipeBuilder, pipeDefinition)
        }
        def sourceId = pipeDefinition.from()
        def processorId = (topologyBuilder as KStreamBuilder).newName('processor')
        try {
            topologyBuilder.addSource(sourceId, pipeDefinition.from())
        } catch (TopologyBuilderException e) {

        }
        topologyBuilder.addProcessor(processorId, new ProcessorSupplier<String, Bytes>() {
            @Override
            Processor<String, Bytes> get() {
                new AbstractProcessor<String, Bytes>() {
                    @Override
                    void process(String key, Bytes value) {
                        def body = new ObjectMapper().readValue(value.get(), Map)
                        def event = new Event(context().topic(), key, body, pipeDefinition.functionConfiguration(), kPipesContext)
                        def destination = function.onEvent(event)
                        if(destination.present) {
                            brokerAdmin.ensureTopicExists(destination.get())
                            kafkaProducer.send(new ProducerRecord<String, Bytes>(destination.get(), key, new Bytes(new ObjectMapper().writeValueAsBytes(body))))
                        }
                    }
                }
            }
        }, sourceId)
    }

}