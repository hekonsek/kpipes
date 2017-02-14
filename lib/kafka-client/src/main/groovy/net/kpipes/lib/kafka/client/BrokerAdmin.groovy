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
package net.kpipes.lib.kafka.client

import kafka.admin.AdminUtils
import kafka.admin.RackAwareMode
import kafka.utils.ZKStringSerializer$
import kafka.utils.ZkUtils
import org.I0Itec.zkclient.ZkClient
import org.apache.kafka.common.errors.TopicExistsException
import org.slf4j.Logger

import static org.slf4j.LoggerFactory.getLogger

class BrokerAdmin {

    private static final Logger LOG = getLogger(BrokerAdmin)

    private final String zooKeeperHost

    private final int zooKeeperPort

    BrokerAdmin(String zooKeeperHost, int zooKeeperPort) {
        this.zooKeeperHost = zooKeeperHost
        this.zooKeeperPort = zooKeeperPort

    }

    void ensureTopicExists(Set<String> topics) {
        def zkClient = new ZkClient("${zooKeeperHost}:${zooKeeperPort}", Integer.MAX_VALUE, 10000, ZKStringSerializer$.MODULE$)
        ZkUtils zooKeeperUtils = ZkUtils.apply(zkClient, false)

        boolean topicCreated = false
        topics.each { topic ->
            try {
                if (!AdminUtils.topicExists(zooKeeperUtils, topic)) {
                    RackAwareMode mode = RackAwareMode.Disabled$.MODULE$
                    AdminUtils.createTopic(zooKeeperUtils, topic, 25, 1, new Properties(), mode)
//                    Thread.sleep(10000)
                    topicCreated = true
                }
            } catch (TopicExistsException e) {
                LOG.debug(e.message)
            }
        }
        if(topicCreated) {
            Thread.sleep(5000)
        }
    }

}
