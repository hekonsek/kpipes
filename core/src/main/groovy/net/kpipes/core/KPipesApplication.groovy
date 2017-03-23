package net.kpipes.core

import net.kpipes.core.spring.KPipesFactory

class KPipesApplication {

    private final KPipes kpipes

    KPipesApplication(String... args) {
        this.kpipes = KPipesFactory.kpipes().startPipes().load()
    }

    void stop() {
        kpipes.stop()
    }

    static void main(String... args) {
        new KPipesApplication(args)
    }

}
