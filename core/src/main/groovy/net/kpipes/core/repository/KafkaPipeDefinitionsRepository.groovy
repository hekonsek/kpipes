package net.kpipes.core.repository

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipes
import net.kpipes.core.PipeDefinition
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.state.QueryableStoreTypes

import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe

class KafkaPipeDefinitionsRepository implements PipeDefinitionsRepository {

    private final KafkaProducer kafkaProducer

    private final KPipes kpipes

    KafkaPipeDefinitionsRepository(KafkaProducer kafkaProducer, KPipes kpipes) {
        this.kafkaProducer = kafkaProducer
        this.kpipes = kpipes
    }

    @Override
    void add(PipeDefinition pipeDefinition) {
        def encodedPipe = encodePipe(pipeDefinition)
        kafkaProducer.send(new ProducerRecord('kpipes.pipeDefinitions', pipeDefinition.id(), new Bytes(new ObjectMapper().writeValueAsBytes([tenant: pipeDefinition.tenant(), pipe: encodedPipe]))))
    }

    @Override
    List<PipeDefinition> list() {
        def streams = kpipes.pipeBuilder().kafkaStreams()
        streams.store('kpipes.pipeDefinitions', QueryableStoreTypes.keyValueStore()).all().collect { KeyValue keyValue ->
            def pipeDef = keyValue.value as Bytes
            def pipe = new ObjectMapper().readValue(pipeDef.get(), Map)
            decodePipe(pipe.tenant as String, pipe.pipe as String)
        }
    }

}