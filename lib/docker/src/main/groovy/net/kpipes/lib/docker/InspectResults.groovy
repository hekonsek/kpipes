package net.kpipes.lib.docker

import groovy.transform.ToString

@ToString
class InspectResults {

    private final Map<String, Object> results

    InspectResults(Map<String, Object> results) {
        this.results = new HashMap<>(results).asImmutable()
    }

    // Results accessors

    Map<String, Object> results() {
        new HashMap<>(results)
    }

    Map<String, String> environment() {
        results.Config.Env.inject([:]){ envs, env ->
            def envParts = env.split('=')
            envs[envParts[0]] = envParts[1]
            envs
        }
    }

}
