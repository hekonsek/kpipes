package net.kpipes.core

class KPipesContext {

    private static final Map<String, ServiceRegistry> serviceRegistriesReferences = [:]

    private static final Map<String, KPipes> kpipesReferences = [:]

    private final String applicationId

    static String nodeId

    private final int kafkaPort

    private final File home

    KPipesContext(String applicationId, int kafkaPort, File home) {
        this.applicationId = applicationId
        this.kafkaPort = kafkaPort
        this.home = home
    }

    static void registerContext(String applicationId, String nodeId, ServiceRegistry serviceRegistry, KPipes kpipes) {
        serviceRegistriesReferences[applicationId] = serviceRegistry
        kpipesReferences[applicationId] = kpipes
        this.nodeId = nodeId
    }

    ServiceRegistry serviceRegistry() {
        serviceRegistriesReferences[applicationId]
    }

    KPipes kpipes() {
        kpipesReferences[applicationId]
    }

    String applicationId() {
        return applicationId
    }

    String nodeId() {
        return nodeId
    }

    int kafkaPort() {
        kafkaPort
    }

    File home() {
        home
    }

}
