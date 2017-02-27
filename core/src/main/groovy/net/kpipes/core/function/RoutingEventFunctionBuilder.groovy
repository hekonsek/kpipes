package net.kpipes.core.function

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.PipeBuilder
import net.kpipes.core.PipeDefinition
import net.kpipes.core.RoutingEventFunction
import net.kpipes.lib.kafka.client.BrokerAdmin
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream

class RoutingEventFunctionBuilder implements StreamFunctionBuilder<RoutingEventFunction> {

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
    void build(PipeBuilder pipeBuilder, PipeDefinition pipeDefinition, RoutingEventFunction function, KStream<String, Bytes> source) {
        source.foreach { String key, Bytes value ->
            def routedEvent = function.apply(pipeDefinition.functionConfiguration(), key, new ObjectMapper().readValue(value.get(), Map))
            brokerAdmin.ensureTopicExists(routedEvent.destination)
            kafkaProducer.send(new ProducerRecord(routedEvent.destination(), key, new Bytes(new ObjectMapper().writeValueAsBytes(routedEvent.event()))))
        }
    }

}