package net.kpipes.core.function

class Event {

    private final String key

    private final Map<String, Object> body

    private final Map<String, Object> config

    Event(String key, Map<String, Object> body, Map<String, Object> config) {
        this.key = key
        this.body = body
        this.config = config
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

}