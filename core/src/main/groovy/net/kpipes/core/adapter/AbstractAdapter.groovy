package net.kpipes.core.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import net.kpipes.core.KPipesContext
import org.slf4j.Logger

import java.lang.reflect.InvocationTargetException

import static org.slf4j.LoggerFactory.getLogger

@CompileStatic
abstract class AbstractAdapter {

    private final Logger LOG = getLogger(getClass())

    protected final KPipesContext kpipesContext

    AbstractAdapter(KPipesContext kpipesContext) {
        this.kpipesContext = kpipesContext
    }

    protected byte[] invokeOperation(String tenant, byte[] serializedRequest) {
        def request = new ObjectMapper().readValue(serializedRequest, Map)
        def serviceName = request.service as String
        def operation = request.operation as String
        def arguments = request.arguments as List
        def service = kpipesContext.serviceRegistry().service(serviceName)
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
        } catch (InvocationTargetException e) {
            LOG.debug('Error invoking operation', e)
            throw e.cause
        }
    }

}
