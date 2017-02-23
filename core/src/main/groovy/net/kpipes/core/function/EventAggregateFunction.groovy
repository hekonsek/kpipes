package net.kpipes.core.function

interface EventAggregateFunction {

    Map<String, Object> onEvent(Event event, Map<String, Object> accumulator)

}