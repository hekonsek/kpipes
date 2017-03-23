package net.kpipes.core

import net.kpipes.core.spring.KPipesFactory

class KPipesApplication {

    static void main(String... args) {
        KPipesFactory.kpipes().startPipes().load()
    }

}
