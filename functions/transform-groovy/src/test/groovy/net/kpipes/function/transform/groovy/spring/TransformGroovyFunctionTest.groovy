package net.kpipes.function.transform.groovy.spring

import net.kpipes.core.function.Event
import net.kpipes.function.transform.groovy.TransformGroovyFunction
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class TransformGroovyFunctionTest {

    def function = new TransformGroovyFunction()

    @Test
    void shouldExecuteExpression() {
        def event = new Event('topic', 'key', [values: [1,2,3]], [expression: 'event.body().count = event.body().values.size(); event.body()'], null)
        def result = function.onEvent(event)
        assertThat(result).containsEntry('count', 3)
    }

}
