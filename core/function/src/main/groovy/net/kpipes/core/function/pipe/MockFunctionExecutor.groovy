package net.kpipes.core.function.pipe

import net.kpipes.core.event.Event

class MockFunctionExecutor implements FunctionExecutor {

    private final List<FunctionEvent> events = []

    @Override
    void executeFunction(String functionAddress, String eventKey, Event event) {
        events << new FunctionEvent(functionAddress, eventKey, event)
    }

    List<FunctionEvent> events() {
        events
    }

    static class FunctionEvent {

        String functionAddress

        String eventKey

        Event event

        FunctionEvent(String functionAddress, String eventKey, Event event) {
            this.functionAddress = functionAddress
            this.eventKey = eventKey
            this.event = event
        }

    }

}