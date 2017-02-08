package net.kpipes.core.function.pipe

import net.kpipes.core.event.Event

interface FunctionExecutor {

    void executeFunction(String functionAddress,  String eventKey, Event event)

}
