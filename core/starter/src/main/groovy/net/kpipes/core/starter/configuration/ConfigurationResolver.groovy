package net.kpipes.core.starter.configuration

interface ConfigurationResolver {

    String string(String key, String defaultValue)

    int integer(String key, int defaultValue)

}