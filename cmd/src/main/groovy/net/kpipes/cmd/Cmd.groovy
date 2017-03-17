package net.kpipes.cmd

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.CaseInsensitiveHeaders

class Cmd {

    private final vertx = Vertx.vertx()

    private final httpClient = vertx.createHttpClient()

    private Object response

    void executeCommand(String... command) {
        httpClient.websocket(8080, 'localhost', '/operation', new CaseInsensitiveHeaders([username: 'anonymous', password: 'anonymous'])) { webSocket ->
            webSocket.handler {
                def response = new ObjectMapper().readValue(it.bytes, Map)
                this.response = response.response
            }

            def commandParts = command as List<String>
            def arguments = commandParts.size() > 2 ? [commandParts[2]] : null
            webSocket.write(Buffer.buffer(new ObjectMapper().writeValueAsBytes([service: commandParts[0], operation: commandParts[1], arguments: arguments])))
        }
    }

    void close() {
        vertx.close()
    }

    static void main(String... args) {
        def cmd = new Cmd()
        cmd.executeCommand(args)
        Thread.sleep(2000)
        if(cmd.response instanceof List) {
            def responseList = cmd.response as List<String>
            println responseList.join('\n')
        } else {
            println cmd.response
        }
        cmd.close()
    }

}
