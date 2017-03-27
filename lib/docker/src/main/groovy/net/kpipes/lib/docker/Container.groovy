package net.kpipes.lib.docker

import com.google.common.collect.ImmutableList
import groovy.transform.ToString

/**
 * Represents CommandLineDocker container to be created.
 */
@ToString
class Container {

    private final String image

    private final String name

    private final String net

    private final Boolean cleanUp

    private final Map<String, String> volumes

    private final Map<String, String> environment

    private final String[] arguments

    Container(String image, String name, String net, Boolean cleanUp, Map<String, String> volumes, Map<String, String> environment, String[] arguments) {
        this.image = image
        this.name = name
        this.net = net
        this.cleanUp = cleanUp
        this.volumes = volumes
        this.environment = environment
        this.arguments = arguments
    }

    static Container container(String image, String name) {
        new Container(image, name, null, null, [:], [:])
    }

    static Container container(String image) {
        new Container(image, null, null, null, [:], [:])
    }

    // Getters

    String image() {
        image
    }

    String name() {
        name
    }

    String net() {
        net
    }
    
    Boolean cleanUp() {
        cleanUp
    }

    Map<String, String> volumes() {
        volumes
    }

    Map<String, String> environment() {
        return environment
    }

    List<String> arguments() {
        ImmutableList.copyOf(arguments)
    }

}
