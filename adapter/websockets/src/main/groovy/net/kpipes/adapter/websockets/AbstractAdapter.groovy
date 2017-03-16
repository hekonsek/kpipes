package net.kpipes.adapter.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext

abstract class AbstractAdapter {

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
                arguments = []
            }
            arguments.add(0, tenant)
        }

        new ObjectMapper().writeValueAsBytes([response: operationMethod.invoke(service, *arguments)])
    }

}
