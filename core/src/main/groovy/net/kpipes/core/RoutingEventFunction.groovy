package net.kpipes.core

interface RoutingEventFunction {

    RoutedEvent apply(Map<String, Object> config, String key, Map<String, Object> event)

    static class RoutedEvent {

        private final Map<String, Object> event

        private final String destination

        RoutedEvent(Map<String, Object> event, String destination) {
            this.event = event
            this.destination = destination
        }

        Map<String, Object> event() {
            event
        }

        String destination() {
            destination
        }

    }

}