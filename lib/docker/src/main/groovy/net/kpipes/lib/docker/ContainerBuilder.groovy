package net.kpipes.lib.docker

class ContainerBuilder {

    private final String image

    private String name

    private String net

    private Boolean cleanUp

    private Map<String, String> volumes = [:]

    private Map<String, String> environment = [:]

    private String[] arguments = []

    ContainerBuilder(String image) {
        this.image = image
    }

    Container build() {
        new Container(image, name, net, cleanUp, volumes, environment, arguments)
    }

    ContainerBuilder name(String name) {
        this.name = name
        this
    }

    ContainerBuilder net(String net) {
        this.net = net
        this
    }

    ContainerBuilder cleanUp(Boolean cleanUp) {
        this.cleanUp = cleanUp
        this
    }

    ContainerBuilder volumes(Map<String, String> volumes) {
        this.volumes = volumes
        this
    }

    ContainerBuilder environment(Map<String, String> environment) {
        this.environment = environment
        this
    }

    ContainerBuilder arguments(String... arguments) {
        this.arguments = arguments
        this
    }

}
