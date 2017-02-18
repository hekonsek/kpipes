package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.processor.ProcessorSupplier

class RoutingEventFunctionBuilder implements FunctionBuilder<RoutingEventFunction> {

    private final KafkaProducer<String, Bytes> kafkaProducer

    private final BrokerAdmin brokerAdmin

    RoutingEventFunctionBuilder(KafkaProducer<String, Bytes> kafkaProducer, BrokerAdmin brokerAdmin) {
        this.kafkaProducer = kafkaProducer
        this.brokerAdmin = brokerAdmin
    }

    @Override
    boolean supports(Object function) {
        function instanceof RoutingEventFunction
    }

    @Override
    void build(RoutingEventFunction function, KStream<String, Bytes> source, PipeDefinition pipeDefinition) {
        source.process(new ProcessorSupplier<String, Bytes>() {
            @Override
            Processor get() {
                new Processor<String, Bytes>() {
                    @Override
                    void init(ProcessorContext context) {
                    }

                    @Override
                    void process(String key, Bytes value) {
                        def routedEvent = function.apply(pipeDefinition.functionConfiguration(), key, new ObjectMapper().readValue(value.get(), Map))
                        brokerAdmin.ensureTopicExists(routedEvent.destination)
                        kafkaProducer.send(new ProducerRecord(routedEvent.destination(), key, new Bytes(new ObjectMapper().writeValueAsBytes(routedEvent.event()))))
                    }

                    @Override
                    void punctuate(long timestamp) {
                    }

                    @Override
                    void close() {
                    }
                }
            }
        })
    }

}