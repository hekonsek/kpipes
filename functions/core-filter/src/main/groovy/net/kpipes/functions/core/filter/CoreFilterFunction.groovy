package net.kpipes.functions.core.filter

import net.kpipes.core.event.Event
import net.kpipes.core.function.spi.Function

class CoreFilterFunction implements Function {

    @Override
    Event apply(Event event) {
        Map<String, Object> config = event.metaData().functionConfig as Map
        def predicateText = config.predicate as String
        def shell = new GroovyShell()
        shell.setVariable('event', event)
        def result = shell.evaluate(predicateText) as boolean
        event.metaData()['result.core.filter'] = result
        if(!result) {
            event.metaData().target = null
        }
        event
    }

}
