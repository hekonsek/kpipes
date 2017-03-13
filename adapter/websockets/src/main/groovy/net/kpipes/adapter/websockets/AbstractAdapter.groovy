package net.kpipes.adapter.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.core.KPipesContext

abstract class AbstractAdapter {

    protected final KPipesContext kpipesContext

    AbstractAdapter(KPipesContext kpipesContext) {
        this.kpipesContext = kpipesContext
    }

    protected byte[] invokeOperation(byte[] serializedRequest) {
        def request = new ObjectMapper().readValue(serializedRequest, Map)
        def serviceName = request.service as String
        def operation = request.operation as String
        def arguments = request.arguments as List
        def service = kpipesContext.serviceRegistry().service(serviceName)
        def operationMethod = service.class.declaredMethods.find{ it.name == operation }
        new ObjectMapper().writeValueAsBytes([response: operationMethod.invoke(service, *arguments)])
    }

}
