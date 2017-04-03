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
package net.kpipes.cmd

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders

import java.util.concurrent.atomic.AtomicBoolean

import static io.vertx.core.buffer.Buffer.buffer
import static org.awaitility.Awaitility.await

class Cmd {

    // Internal collaborators

    private final json = new ObjectMapper()

    private final vertx = Vertx.vertx()

    private final httpClient = vertx.createHttpClient()

    // Configuration members

    private final String host

    private final int port

    // Constructors

    Cmd(host, port) {
        this.host = host
        this.port = port
    }

    Object executeCommand(List<String> command) {
        def responseReceived = new AtomicBoolean(false)
        Object response = null
        httpClient.websocket(port, host, '/operation', new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])) { webSocket ->
            webSocket.handler {
                response = json.readValue(it.bytes, Map).response
                responseReceived.set(true)
            }
            webSocket.write(buffer(json.writeValueAsBytes([service: command[0], operation: command[1], arguments: parseArguments(command)])))
        } {
            response = "Cannot connect to KPipes server ${host}:${port}. Have you started your KPipes server? Is your firewall configured properly? Is your network connectivity OK?"
            responseReceived.set(true)
        }
        await().untilTrue(responseReceived)
        response
    }

    String formatCommand(List<String> command) {
        def response = executeCommand(command)
        if(response instanceof List) {
            def responseList = response as List<String>
            return responseList.join('\n')
        } else if(response instanceof Map) {
            def responseList = response as Map<String, Object>
            return responseList.collect{ "${it.key}\t${it.value}" }.join('\n')
        }  else if(response == null) {
            return 'Success.'
        } else {
            return response.toString()
        }
    }

    void close() {
        vertx.close()
    }

    // Helpers

    protected static List<Object> parseArguments(List<String> command) {
        def commandParts = command.collect{
            if(it.startsWith('[')) {
                it = new GroovyShell().evaluate(it) as Map
            }
            it
        }
        commandParts.size() > 2 ? commandParts.subList(2, commandParts.size()) : null
    }

    protected static Map<String, String> parseOptions(List<String> command) {
        def options = [:]
        if(command.find{ it.startsWith('--host=') }) {
            options.host = command.find{ it.startsWith('--host=') }.replaceFirst('--host=', '')
        }
        options
    }

    // Execution point

    static void main(String... args) {
        def argsList = args.toList()
        def options = parseOptions(argsList)
        def host = options.get('host', 'localhost')
        def port = 8080
        argsList.removeIf{ it.startsWith('--host=') }
        def cmd = new Cmd(host, port)
        println cmd.formatCommand(argsList)
        cmd.close()
    }

}
