package net.kpipes.core.function

import net.kpipes.core.KPipes

class Event {

    private final String topic

    private final Optional<String> target

    private final String key

    private final Map<String, Object> body

    private final Map<String, Object> config

    private final boolean added

    private final KPipes kPipes

    Event(String topic, Optional<String> target, String key, Map<String, Object> body, Map<String, Object> config, boolean added, KPipes kPipes) {
        this.topic = topic
        this.target = target
        this.key = key
        this.body = body
        this.config = config
        this.added = added
        this.kPipes = kPipes
    }

    Event(String topic, String key, Map<String, Object> body, Map<String, Object> config, KPipes kPipes) {
        this(topic, Optional.empty(), key, body, config, true, kPipes)
    }

    Event(String topic, Optional<String> to, String key, Map<String, Object> body, Map<String, Object> config, KPipes kPipes) {
        this(topic, to, key, body, config, true, kPipes)
    }

    String topic() {
        topic
    }

    Optional<String> target() {
        target
    }

    String tenant() {
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