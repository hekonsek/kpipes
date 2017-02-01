package net.kpipes.core.starter.configuration

import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.EnvironmentConfiguration
import org.apache.commons.configuration2.MapConfiguration
import org.apache.commons.configuration2.SystemConfiguration

class CommonsConfigurationResolver implements ConfigurationResolver {

    private final Configuration configuration

    CommonsConfigurationResolver() {
        configuration = new EnvFriendlyConfiguration()
        configuration.addConfiguration(new MapConfiguration([:]), true)
        configuration.addConfiguration(new SystemConfiguration())
        configuration.addConfiguration(new EnvironmentConfiguration())
    }

    @Override
    String string(String key, String defaultValue) {
        configuration.getString(key, defaultValue)
    }

    @Override
    int integer(String key, int defaultValue) {
        configuration.getInt(key, defaultValue)
    }

}