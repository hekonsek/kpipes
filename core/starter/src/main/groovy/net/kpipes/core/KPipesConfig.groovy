package net.kpipes.core

class KPipesConfig {

    KPipesConfig(String kafkaHost, int kafkaPort, String zooKeeperHost, int zooKeeperPort) {
        this.kafkaHost = kafkaHost
        this.kafkaPort = kafkaPort
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort
    }

    private final String kafkaHost

    private final int kafkaPort

    private final String zooKeeperHost

    private final int zooKeeperPort

    String getKafkaHost() {
        return kafkaHost
    }

    int getKafkaPort() {
        return kafkaPort
    }

    String getZooKeeperHost() {
        return zooKeeperHost
    }

    int getZooKeeperPort() {
        return zooKeeperPort
    }

}
