package net.kpipes.lib.kafka.broker

import org.apache.zookeeper.server.ServerCnxnFactory
import org.apache.zookeeper.server.ZooKeeperServer as ApacheZooKeeperServer

class ZooKeeperServer {

    private final int port

    private final String dataDirectory

    ZooKeeperServer(int port, String dataDirectory) {
        this.port = port
        this.dataDirectory = dataDirectory
    }

    ZooKeeperServer start() {
        def server = new ApacheZooKeeperServer(new File(dataDirectory), new File(dataDirectory), 2000)

        def factory = ServerCnxnFactory.createFactory()
        factory.configure(new InetSocketAddress('0.0.0.0', port), 1024)
        factory.startup(server)
        this
    }

}