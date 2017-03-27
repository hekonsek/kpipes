package net.kpipes.lib.docker

/**
 * Higher-level service around Docker client. Used to invoke common Docker operations like run-once command execution
 * or running named daemon services.
 */
interface Docker {

    /**
     * Starts defined container and returns output of the command. Used for executing and parsing short-living
     * dockerized commands.
     *
     * @param container container definition.
     * @return output of the container execution, one line per list element.
     */
    List<String> execute(Container container)

    /**
     * Assures that service container is created and started. Service is long running singleton container identifiable by name.
     *
     * If the operation is executed and the service is already running, then nothing happens. If the service has been
     * already created, then it is started. If service container doesn't exist, it is created and then started.
     *
     * @param container container definition
     * @throws IllegalArgumentException if container definition doesn't contain 'name', which is required by service
     * @return results of the service startup
     */
    ServiceStartupResults startService(Container container)

    ContainerStatus status(String name)

    void stop(String name)

    InspectResults inspect(String containerId)

}