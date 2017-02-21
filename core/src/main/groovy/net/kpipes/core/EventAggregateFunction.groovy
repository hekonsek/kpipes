package net.kpipes.core

interface EventAggregateFunction {

    Map<String, Object> onEvent(Map<String, Object> config, Map<String, Object> accumulator,  String key, Map<String, Object> event)

}