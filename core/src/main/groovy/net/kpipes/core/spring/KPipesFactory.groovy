package net.kpipes.core.spring

import net.kpipes.core.KPipes
import org.springframework.boot.builder.SpringApplicationBuilder

import static net.kpipes.lib.commons.Uuids.uuid

class KPipesFactory {

    static KPipes kpipes() {
        def applicationContext = new SpringApplicationBuilder(SpringKPipesConfig).run()
        applicationContext.getBean(KPipes)
    }

}
