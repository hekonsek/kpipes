/**
 * Licensed to the KPipes under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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