package net.kpipes.lib.kafka.broker

import kafka.server.KafkaConfig
import kafka.server.KafkaServer
import kafka.utils.SystemTime$
import scala.Option
import scala.collection.JavaConversions

public class KafkaBroker {

    private final int port;

    private final String zooKeeperHost;

    private final int zooKeeperPort;

    private final String dataDirectory

    KafkaBroker(int port, String zooKeeperHost, int zooKeeperPort, String dataDirectory) {
        this.port = port
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort
        this.dataDirectory = dataDirectory
    }

    public KafkaBroker start() {
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("zookeeper.connect", zooKeeperHost + ":" + zooKeeperPort);
        kafkaConfig.setProperty("broker.id", "1");
        kafkaConfig.setProperty("host.name", "localhost");
        kafkaConfig.setProperty("advertised.host.name", "localhost");
        kafkaConfig.setProperty("auto.create.topics.enable", "true");
        kafkaConfig.setProperty("port", port + "");
        kafkaConfig.setProperty("log.dir", dataDirectory);
        kafkaConfig.setProperty("log.flush.interval.messages", 1 + "");

        KafkaServer broker = new KafkaServer(new KafkaConfig(kafkaConfig), SystemTime$.MODULE$, Option.empty(), JavaConversions.asScalaBuffer(Collections.emptyList()));
        broker.startup();
        return this;
    }

}
