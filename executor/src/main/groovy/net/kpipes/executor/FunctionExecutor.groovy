package net.kpipes.executor

import net.kpipes.core.function.Event

interface FunctionExecutor {

    boolean supports(Event event)

    Map<String, Object> onEvent(Event event)

}