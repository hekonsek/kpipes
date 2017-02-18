package net.kpipes.core

interface RoutingEventFunction {

    RoutedEvent apply(Map<String, Object> config, String key, Map<String, Object> event)

    static class RoutedEvent {

        Map<String, Object> event

        String destination

        RoutedEvent(Map<String, Object> event, String destination) {
            this.event = event
            this.destination = destination
        }

        Map<String, Object> event() {
            return event
        }

        String destination() {
            return destination
        }

    }

}