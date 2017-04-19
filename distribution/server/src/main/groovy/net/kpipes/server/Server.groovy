package net.kpipes.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

import static net.kpipes.lib.commons.Uuids.uuid

@SpringBootApplication(scanBasePackages = 'net.kpipes')
class Server {

    static void main(String... args) {
        System.setProperty('nodeId', uuid())
        new SpringApplicationBuilder(Server).build().run(args)
    }

}