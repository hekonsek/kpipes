package net.kpipes.core.repository

import net.kpipes.core.Json
import net.kpipes.core.PipeDefinition
import net.kpipes.core.store.FileSystemKeyValueStore
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.PipeDefinitionEncoder.encodePipe

class KafkaPipeDefinitionsRepository implements PipeDefinitionsRepository {

    private final KafkaProducer kafkaProducer

    private final FileSystemKeyValueStore store

    private final Json json

    // Constructors

    KafkaPipeDefinitionsRepository(KafkaProducer kafkaProducer, FileSystemKeyValueStore store, Json json) {
        this.kafkaProducer = kafkaProducer
        this.store = store
        this.json = json
    }

    // Operations

    @Override
    void add(PipeDefinition pipeDefinition) {
        def encodedPipe = encodePipe(pipeDefinition)
        def record = new ProducerRecord('kpipes.pipeDefinitions', pipeDefinition.id(), json.asBytes(tenant: pipeDefinition.tenant(), pipe: encodedPipe))
        kafkaProducer.send(record)
    }

    @Override
    void remove(String id) {
        kafkaProducer.send(new ProducerRecord('kpipes.pipeDefinitions', id, null))
    }

    @Override
    List<PipeDefinition> list() {
        store.all('kpipes.pipeDefinitions').collect {
            def pipe = json.read(it.value)
            decodePipe(pipe.tenant as String, pipe.pipe as String)
        }
    }

}