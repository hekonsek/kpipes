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
package net.kpipes.lib.commons

import groovy.transform.CompileStatic
import org.apache.commons.lang3.RandomUtils
import org.slf4j.Logger

import java.util.concurrent.atomic.AtomicInteger

import static org.slf4j.LoggerFactory.getLogger

/**
 * IP networking related utilities.
 */
@CompileStatic
final class Networks {

    private static final int MINIMUM_PORT_NUMBER = 1100

    private static final int MAXIMUM_PORT_NUMBER = 65535

    // Logger

    private static final Logger LOG = getLogger(Networks)

    // Constructors

    private Networks() {
    }

    // Utilities API

    private static AtomicInteger lastPort = new AtomicInteger(RandomUtils.nextInt(MINIMUM_PORT_NUMBER, MAXIMUM_PORT_NUMBER))

    static synchronized int availableTcpPort() {
        int port = lastPort.incrementAndGet()
        if(port > MAXIMUM_PORT_NUMBER) {
            lastPort.set(MINIMUM_PORT_NUMBER)
            port = lastPort.getAndIncrement()
        }

        ServerSocket socket = null
        try {
            socket = new ServerSocket(port)
            socket.setReuseAddress(true)
            LOG.debug('Found available TCP port: {}', port)
            return port
        } catch (IOException e) {
            LOG.debug('Port {} had been already taken - looking for the next one...', port)
        } finally {
            if (socket != null) {
                try {
                    socket.close()
                } catch (IOException e) {
                    LOG.debug('Error while closing socket:', e)
                }
            }
        }
        return availableTcpPort()
    }

}
