package net.kpipes.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication(scanBasePackages = 'net.kpipes')
class Server {

    static void main(String... args) {
        new SpringApplicationBuilder(Server).build().run(args)
    }

}