package net.kpipes.function.filter

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventRoutingFunction

class FilterFunction implements EventRoutingFunction {

    @Override
    Optional<String> onEvent(Event event) {
        def predicateText = event.config().predicate as String
        def shell = new GroovyShell()
        shell.setVariable('key', event.key())
        shell.setVariable('event', event.body())
        boolean result = shell.evaluate(predicateText) as boolean
        result ? Optional.of("${event.tenant()}.${event.target().get()}") : Optional.empty()
    }

}