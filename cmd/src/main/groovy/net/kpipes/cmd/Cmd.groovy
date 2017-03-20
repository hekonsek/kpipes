package net.kpipes.cmd

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.CaseInsensitiveHeaders

import java.util.concurrent.Callable

import static io.vertx.core.buffer.Buffer.buffer
import static org.awaitility.Awaitility.await

class Cmd {

    private final vertx = Vertx.vertx()

    private final httpClient = vertx.createHttpClient()

    private final String host

    private final int port

    Cmd(host, port) {
        this.host = host
        this.port = port
    }

    Cmd(int port) {
        this('localhost', port)
    }

    Cmd() {
        this(8080)
    }

    Object executeCommand(String... command) {
        Object response
        httpClient.websocket(port, host, '/operation', new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])) { webSocket ->
            webSocket.handler {
                response = new ObjectMapper().readValue(it.bytes, Map).response
            }

            def commandParts = command as List<String>
            def arguments = commandParts.size() > 2 ? [commandParts[2]] : null
            webSocket.write(buffer(new ObjectMapper().writeValueAsBytes([service: commandParts[0], operation: commandParts[1], arguments: arguments])))
        } {
            response = "Cannot connect to KPipes server ${host}:${port}. Have you started your KPipes server? Is your firewall configured properly? Is your network connectivity OK?"
        }
        await().until({ response != null } as Callable<Boolean>)
        response
    }

    void close() {
        vertx.close()
    }

    static void main(String... args) {
        def cmd = new Cmd()
        def response = cmd.executeCommand(args)
        if(response instanceof List) {
            def responseList = response as List<String>
            println responseList.join('\n')
        } else {
            println response
        }
        cmd.close()
    }

}
