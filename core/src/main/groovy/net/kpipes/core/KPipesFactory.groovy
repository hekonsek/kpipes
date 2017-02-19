package net.kpipes.core

import net.kpipes.core.spring.SpringServiceRegistry

class KPipesFactory {

    static KPipes kpipes() {
        def functionRegistry = new SpringServiceRegistry()
        new KPipes(functionRegistry.applicationContext.getBean(KPipesConfig), functionRegistry)
    }

}
