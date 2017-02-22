package net.kpipes.core.function

interface EventMappingFunction {

    Map<String, Object> onEvent(Event event)

}