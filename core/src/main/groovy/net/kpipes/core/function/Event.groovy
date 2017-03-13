package net.kpipes.core.function

import net.kpipes.core.KPipes
import net.kpipes.core.KPipesContext
import net.kpipes.core.ServiceRegistry
import org.apache.kafka.streams.KafkaStreams

class Event {

    private final String topic

    private final String key

    private final Map<String, Object> body

    private final Map<String, Object> config

    private final boolean added

    private final KPipesContext kpipesContext

    Event(String topic, String key, Map<String, Object> body, Map<String, Object> config, boolean added, KPipesContext kpipesContext) {
        this.topic = topic
        this.key = key
        this.body = body
        this.config = config
        this.added = added
        this.kpipesContext = kpipesContext
    }

    Event(String topic, String key, Map<String, Object> body, Map<String, Object> config, KPipesContext kpipesContext) {
        this(topic, key, body, config, true, kpipesContext)
    }

    String key() {
        key
    }

    Map<String, Object> body() {
        body
    }

    Map<String, Object> config() {
        config
    }

    boolean added() {
        added
    }

    KPipes kPipes() {
        kpipesContext.kpipes()
    }

    ServiceRegistry serviceRegistry() {
        kpipesContext.serviceRegistry()
    }

    KafkaStreams kafkaStreams() {
        kpipesContext.kpipes().pipeBuilder().kafkaStreams()
    }

}