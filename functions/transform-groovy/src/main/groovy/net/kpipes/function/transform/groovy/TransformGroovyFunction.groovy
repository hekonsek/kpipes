package net.kpipes.function.transform.groovy

import net.kpipes.core.function.Event
import net.kpipes.core.function.EventMappingFunction

class TransformGroovyFunction implements EventMappingFunction {

    @Override
    Map<String, Object> onEvent(Event event) {
        def expressionText = event.config().expression as String
        def shell = new GroovyShell()
        shell.setVariable('event', event)
        shell.evaluate(expressionText) as Map<String, Object>
    }

}