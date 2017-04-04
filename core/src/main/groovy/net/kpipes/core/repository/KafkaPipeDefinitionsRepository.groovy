package net.kpipes.core.repository

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.Json
import net.kpipes.core.KPipes
import net.kpipes.core.PipeDefinition
import net.kpipes.core.store.FileSystemKeyValueStore
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.state.QueryableStoreTypes

import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe

class KafkaPipeDefinitionsRepository implements PipeDefinitionsRepository {

    private final KafkaProducer kafkaProducer

    private final FileSystemKeyValueStore store

    private final Json json

    KafkaPipeDefinitionsRepository(KafkaProducer kafkaProducer, FileSystemKeyValueStore store, Json json) {
        this.kafkaProducer = kafkaProducer
        this.store = store
        this.json = json
    }

    @Override
    void add(PipeDefinition pipeDefinition) {
        def encodedPipe = encodePipe(pipeDefinition)
        kafkaProducer.send(new ProducerRecord('kpipes.pipeDefinitions', pipeDefinition.id(), new Bytes(new ObjectMapper().writeValueAsBytes([tenant: pipeDefinition.tenant(), pipe: encodedPipe]))))
    }

    @Override
    List<PipeDefinition> list() {
        store.all('kpipes.pipeDefinitions').collect {
            def pipe = json.read(it.value)
            decodePipe(pipe.tenant as String, pipe.pipe as String)
        }
    }

}