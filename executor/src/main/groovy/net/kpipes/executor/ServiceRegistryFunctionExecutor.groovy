package net.kpipes.executor

import net.kpipes.core.KPipes
import net.kpipes.core.function.Event
import org.slf4j.Logger

import java.lang.reflect.InvocationTargetException

import static org.slf4j.LoggerFactory.getLogger

class ServiceRegistryFunctionExecutor implements FunctionExecutor {

    private final Logger LOG = getLogger(getClass())

    private final KPipes kpipes

    ServiceRegistryFunctionExecutor(KPipes kpipes) {
        this.kpipes = kpipes
    }

    @Override
    boolean supports(Event event) {
        kpipes.serviceRegistry().service(event.config().service as String) != null
    }

    @Override
    Map<String, Object> onEvent(Event event) {
        def serviceName = event.config().service as String
        def operation = event.config().operation as String
        def service = kpipes.serviceRegistry().service(serviceName)
        def operationMethod = service.class.declaredMethods.find{ it.name == operation }

        try {
            def response = operationMethod.invoke(service, [event].toArray())
            LOG.debug('Received operation response: {}', response)
            [response: (response as Event).body()]
        } catch (Throwable e) {
            LOG.debug('Error invoking operation', e)
            if(e instanceof InvocationTargetException) {
                e = e.cause
            }
            [response: "${e.class.simpleName}: ${e.message}" as String, error: true]
        }
    }

}