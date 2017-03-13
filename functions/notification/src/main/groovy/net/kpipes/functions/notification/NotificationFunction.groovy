package net.kpipes.functions.notification

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventRoutingFunction

class NotificationFunction implements EventRoutingFunction {

    @Override
    Optional<String> onEvent(Event event) {
        def channel = event.config().channel
        Optional.of("${event.tenant()}.notification.${channel}")
    }

}