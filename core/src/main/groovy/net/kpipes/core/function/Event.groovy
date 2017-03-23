package net.kpipes.core.function

import net.kpipes.core.KPipes
import net.kpipes.core.KPipesConfig
import net.kpipes.core.ServiceRegistry
import org.apache.kafka.streams.KafkaStreams

class Event {

    private final String topic

    private final String key

    private final Map<String, Object> body

    private final Map<String, Object> config

    private final boolean added

    private final KPipes kPipes

    Event(String topic, String key, Map<String, Object> body, Map<String, Object> config, boolean added, KPipes kPipes) {
        this.topic = topic
        this.key = key
        this.body = body
        this.config = config
        this.added = added
        this.kPipes = kPipes
    }

    Event(String topic, String key, Map<String, Object> body, Map<String, Object> config, KPipes kPipes) {
        this(topic, key, body, config, true, kPipes)
    }

    String topic() {
        topic
    }


    def tenant() {
        topic.substring(0, topic.indexOf('.'))
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
        kPipes
    }

}