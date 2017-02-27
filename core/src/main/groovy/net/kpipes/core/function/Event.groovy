package net.kpipes.core.function

import net.kpipes.core.KPipesContext
import net.kpipes.core.ServiceRegistry

class Event {

    private final String key

    private final Map<String, Object> body

    private final Map<String, Object> config

    private final boolean added

    private final KPipesContext kpipesContext

    Event(String key, Map<String, Object> body, Map<String, Object> config, boolean added, KPipesContext kpipesContext) {
        this.key = key
        this.body = body
        this.config = config
        this.added = added
        this.kpipesContext = kpipesContext
    }

    Event(String key, Map<String, Object> body, Map<String, Object> config, KPipesContext kpipesContext) {
        this(key, body, config, true, kpipesContext)
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

    ServiceRegistry serviceRegistry() {
        kpipesContext.serviceRegistry()
    }

}