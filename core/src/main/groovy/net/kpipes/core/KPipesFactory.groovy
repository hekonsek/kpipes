package net.kpipes.core

import net.kpipes.core.spring.SpringServiceRegistry

import static net.kpipes.core.KPipesContext.registerContext
import static net.kpipes.lib.commons.Uuids.uuid

class KPipesFactory {

    static KPipes kpipes() {
        kpipes(uuid())
    }

    static KPipes kpipes(String applicationId) {
        def functionRegistry = new SpringServiceRegistry(applicationId)
        def kpipes = new KPipes(applicationId, functionRegistry.applicationContext.getBean(KPipesConfig), functionRegistry)
        registerContext(applicationId, functionRegistry, kpipes)
        kpipes
    }

}
