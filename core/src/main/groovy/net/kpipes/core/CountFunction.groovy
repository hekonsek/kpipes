package net.kpipes.core

class CountFunction implements EventAggregateFunction {

    @Override
    Map<String, Object> onEvent(Map<String, Object> config, Map<String, Object> accumulator, String key, Map<String, Object> event) {
        Long current = accumulator['count']
        if(current == null) {
            current = 0
        }
        accumulator['count'] = current + 1
        accumulator
    }

}