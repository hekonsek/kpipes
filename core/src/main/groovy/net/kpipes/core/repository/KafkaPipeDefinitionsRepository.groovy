package net.kpipes.core.repository

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext
import net.kpipes.core.PipeDefinition
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.state.QueryableStoreTypes

class KafkaPipeDefinitionsRepository implements PipeDefinitionsRepository {

    private final KafkaProducer kafkaProducer

    private final KPipesContext kpipesContext

    KafkaPipeDefinitionsRepository(KafkaProducer kafkaProducer, KPipesContext kpipesContext) {
        this.kafkaProducer = kafkaProducer
        this.kpipesContext = kpipesContext
    }

    @Override
    void add(PipeDefinition pipeDefinition) {
        def string = "${pipeDefinition.from()} | ${pipeDefinition.functionAddress()} ${pipeDefinition.functionConfiguration()} | ${pipeDefinition.to().get()}" as String
        kafkaProducer.send(new ProducerRecord('kpipes.pipeDefinitions', pipeDefinition.id(), new Bytes(new ObjectMapper().writeValueAsBytes([tenant: pipeDefinition.tenant(), pipe: string]))))
    }

    @Override
    List<PipeDefinition> list() {
        def streams = kpipesContext.kpipes().pipeBuilder().kafkaStreams()
        streams.store('kpipes.pipeDefinitions', QueryableStoreTypes.keyValueStore()).all().collect { KeyValue keyValue ->
                def pipeDef = keyValue.value as Bytes
                def pipe = new ObjectMapper().readValue(pipeDef.get(), Map)
                PipeDefinition.parsePipeDefinition(pipe.tenant as String, pipe.pipe as String)
        }
    }

}