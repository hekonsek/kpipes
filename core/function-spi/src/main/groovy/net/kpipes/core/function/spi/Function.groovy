package net.kpipes.core.function.spi

import net.kpipes.core.event.Event

interface Function {

    Event apply(Event event)

}