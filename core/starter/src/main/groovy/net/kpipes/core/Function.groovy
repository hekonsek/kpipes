package net.kpipes.core

interface Function {

    Map<String, Object> apply(Map<String, Object> config, String key, Map<String, Object> event)

}