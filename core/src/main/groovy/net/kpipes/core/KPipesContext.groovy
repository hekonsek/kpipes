package net.kpipes.core

class KPipesContext {

    private static final Map<String, ServiceRegistry> serviceRegistriesReferences = [:]

    private static final Map<String, KPipes> kpipesReferences = [:]

    private final String applicationId

    KPipesContext(String applicationId) {
        this.applicationId = applicationId
    }

    static void registerContext(String applicationId, ServiceRegistry serviceRegistry, KPipes kpipes) {
        serviceRegistriesReferences[applicationId] = serviceRegistry
        kpipesReferences[applicationId] = kpipes
    }

    ServiceRegistry serviceRegistry() {
        serviceRegistriesReferences[applicationId]
    }

    KPipes kpipes() {
        kpipesReferences[applicationId]
    }

}
