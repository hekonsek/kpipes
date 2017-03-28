package net.kpipes.core.spring

import net.kpipes.core.KPipes
import org.springframework.boot.builder.SpringApplicationBuilder

import static net.kpipes.lib.commons.Uuids.uuid

class KPipesFactory {

    static KPipes kpipes() {
        kpipes(uuid(), uuid())
    }

    static KPipes kpipes(String applicationId, String nodeId) {
        def applicationContext = new SpringApplicationBuilder(SpringKPipesConfig).run("--applicationId=${applicationId}", "--nodeId=${nodeId}")
        applicationContext.getBean(KPipes)
    }

}
