package net.kpipes.core.function

class Event {

    private final String key

    private final Map<String, Object> body

    private final Map<String, Object> config

    private final boolean added

    Event(String key, Map<String, Object> body, Map<String, Object> config, boolean added) {
        this.key = key
        this.body = body
        this.config = config
        this.added = added
    }

    Event(String key, Map<String, Object> body, Map<String, Object> config) {
        this(key, body, config, true)
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

}