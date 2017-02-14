package net.kpipes.core

import net.kpipes.core.spring.SpringFunctionRegistry

class KPipesFactory {

    static KPipes kpipes() {
        def functionRegistry = new SpringFunctionRegistry()
        new KPipes(functionRegistry.applicationContext.getBean(KPipesConfig), functionRegistry)
    }

}
