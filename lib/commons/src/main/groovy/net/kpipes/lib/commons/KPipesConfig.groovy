package net.kpipes.lib.commons

class KPipesConfig {

    private final String applicationId

    private final String nodeId

    private final String kafkaHost

    private final int kafkaPort

    private final String  kafkaDataDirectory

    private final String zooKeeperHost

    private final int zooKeeperPort

    private final String zooKeeperDirectory

    private final File home

    KPipesConfig(String applicationId, String nodeId, String kafkaHost, int kafkaPort, String kafkaDataDirectory, String zooKeeperHost, int zooKeeperPort, String zooKeeperDirectory, File home) {
        this.applicationId = applicationId
        this.nodeId = nodeId
        this.kafkaHost = kafkaHost
        this.kafkaPort = kafkaPort
        this.kafkaDataDirectory = kafkaDataDirectory
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort
        this.zooKeeperDirectory = zooKeeperDirectory
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

    File kafkaDataDirectory() {
        new File(applicationHome(), kafkaDataDirectory)
    }

    String getZooKeeperHost() {
        return zooKeeperHost
    }

    int getZooKeeperPort() {
        return zooKeeperPort
    }

    File zooKeeperDirectory() {
        new File(applicationHome(), zooKeeperDirectory)
    }

    File home() {
        return home
    }

    File applicationHome() {
        new File(home, applicationId)
    }

}
