package net.kpipes.core

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable

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
    void build(PipeDefinition pipeDefinition, RoutingEventFunction function, KStream<String, Bytes> source) {
        source.foreach { String key, Bytes value ->
            def routedEvent = function.apply(pipeDefinition.functionConfiguration(), key, new ObjectMapper().readValue(value.get(), Map))
            brokerAdmin.ensureTopicExists(routedEvent.destination)
            kafkaProducer.send(new ProducerRecord(routedEvent.destination(), key, new Bytes(new ObjectMapper().writeValueAsBytes(routedEvent.event()))))
        }
    }

    @Override
    void build(PipeDefinition pipeDefinition, RoutingEventFunction function, KTable<String, Bytes> source) {
        source.foreach { String key, Bytes value ->
            def routedEvent = function.apply(pipeDefinition.functionConfiguration(), key, new ObjectMapper().readValue(value.get(), Map))
            brokerAdmin.ensureTopicExists(routedEvent.destination)
            kafkaProducer.send(new ProducerRecord(routedEvent.destination(), key, new Bytes(new ObjectMapper().writeValueAsBytes(routedEvent.event()))))
        }
    }

}