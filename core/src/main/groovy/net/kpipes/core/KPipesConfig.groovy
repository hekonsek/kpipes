package net.kpipes.core

class KPipesConfig {

    private final String applicationId

    private final String nodeId

    private final String kafkaHost

    private final int kafkaPort

    private final String zooKeeperHost

    private final int zooKeeperPort

    private final File home

    KPipesConfig(String applicationId, String nodeId, String kafkaHost, int kafkaPort, String zooKeeperHost, int zooKeeperPort, File home) {
        this.applicationId = applicationId
        this.nodeId = nodeId
        this.kafkaHost = kafkaHost
        this.kafkaPort = kafkaPort
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort
        this.home = home
    }

    String applicationId() {
        return applicationId
    }

    String nodeId() {
        return nodeId
    }

    String getKafkaHost() {
        return kafkaHost
    }

    int kafkaPort() {
        return kafkaPort
    }

    String getZooKeeperHost() {
        return zooKeeperHost
    }

    int getZooKeeperPort() {
        return zooKeeperPort
    }

    File home() {
        return home
    }

    File applicationHome() {
        new File(home, applicationId)
    }

}
