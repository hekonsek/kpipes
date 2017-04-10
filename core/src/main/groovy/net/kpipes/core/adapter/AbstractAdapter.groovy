package net.kpipes.core.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.KPipes
import org.slf4j.Logger

import java.lang.reflect.InvocationTargetException

import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
abstract class AbstractAdapter {

    private final Logger LOG = getLogger(getClass())

    protected final KPipes kpipes

    AbstractAdapter(KPipes kpipes) {
        this.kpipes = kpipes
    }

    protected byte[] invokeOperation(String tenant, byte[] serializedRequest) {
        def request = new ObjectMapper().readValue(serializedRequest, Map)
        def serviceName = request.service as String
        def operation = request.operation as String
        def arguments = request.arguments as List
        def service = kpipes.serviceRegistry().service(serviceName)
        def operationMethod = service.class.declaredMethods.find{ it.name == operation }

        def operationAnnotations = operationMethod.parameterAnnotations as List
        if(!operationAnnotations.isEmpty() && operationAnnotations.first().find{ it instanceof Tenant }) {
            if(arguments == null) {
                arguments = [tenant]
            } else {
                arguments.add(0, tenant)
            }
        }

        try {
            def response = operationMethod.invoke(service, arguments != null ? arguments.toArray() : null)
            LOG.debug('Received operation response: {}', response)
            new ObjectMapper().writeValueAsBytes([response: response])
        } catch (Throwable e) {
            LOG.debug('Error invoking operation', e)
            if(e instanceof InvocationTargetException) {
                e = e.cause
            }
            new ObjectMapper().writeValueAsBytes([response: "${e.class.simpleName}: ${e.message}" as String, error: true])
        }
    }

}
