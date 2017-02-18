package net.kpipes.functions.notification

import net.kpipes.core.RoutingEventFunction

class NotificationFunction implements RoutingEventFunction {

    @Override
    RoutingEventFunction.RoutedEvent apply(Map<String, Object> config, String key, Map<String, Object> event) {
        def channel = config.channel
        def tenant = key.split(/\|/)[0]
        new RoutingEventFunction.RoutedEvent(event, "${tenant}.notification.${channel}")
    }

}