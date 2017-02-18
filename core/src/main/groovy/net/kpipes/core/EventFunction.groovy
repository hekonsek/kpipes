package net.kpipes.core

interface EventFunction {

    Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event)

}