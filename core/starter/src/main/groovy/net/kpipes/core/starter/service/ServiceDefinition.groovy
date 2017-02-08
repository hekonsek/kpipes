package net.kpipes.core.starter.service

class ServiceDefinition {

    private final Class<?> type

    private final Object instance

    private final String onStartCallback

    private final boolean overrideable

    ServiceDefinition(Class<?> type, Object instance, String onStartCallback, boolean overrideable) {
        this.type = type
        this.instance = instance
        this.onStartCallback = onStartCallback
        this.overrideable = overrideable
    }

    Class<?> type() {
        type
    }

    Object instance() {
        instance
    }

    String onStartCallback() {
        onStartCallback
    }

    boolean overrideable() {
        overrideable
    }

    @Override
    String toString() {
        instance.toString()
    }

}
