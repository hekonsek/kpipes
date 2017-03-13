package net.kpipes.core.function

interface EventRoutingFunction {

    Optional<String> onEvent(Event event)

}