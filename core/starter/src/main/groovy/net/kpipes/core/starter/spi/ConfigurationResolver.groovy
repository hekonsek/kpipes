package net.kpipes.core.starter.spi

interface ConfigurationResolver {

    String string(String key, String defaultValue)

    int integer(String key, int defaultValue)

}