package net.kpipes.service.kpipes

import static net.kpipes.lib.commons.Mavens.artifactVersionFromDependenciesProperties

class KPipesService {

    String version() {
        artifactVersionFromDependenciesProperties('net.kpipes', 'kpipes-lib-commons').orElseThrow {
            new RuntimeException()
        }
    }

}
