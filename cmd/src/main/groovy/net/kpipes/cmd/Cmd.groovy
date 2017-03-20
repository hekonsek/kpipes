package net.kpipes.cmd

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.CaseInsensitiveHeaders

import java.util.concurrent.Callable

import static org.awaitility.Awaitility.await

class Cmd {

    private final vertx = Vertx.vertx()

    private final httpClient = vertx.createHttpClient()

    Object executeCommand(String... command) {
        Object response
        httpClient.websocket(8080, 'localhost', '/operation', new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])) { webSocket ->
            webSocket.handler {
                response = new ObjectMapper().readValue(it.bytes, Map).response
            }

            def commandParts = command as List<String>
            def arguments = commandParts.size() > 2 ? [commandParts[2]] : null
            webSocket.write(Buffer.buffer(new ObjectMapper().writeValueAsBytes([service: commandParts[0], operation: commandParts[1], arguments: arguments])))
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
