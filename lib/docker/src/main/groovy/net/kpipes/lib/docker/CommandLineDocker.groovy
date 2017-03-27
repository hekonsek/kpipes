/**
 * Licensed to the KPipes under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kpipes.lib.docker

import com.fasterxml.jackson.databind.ObjectMapper
import net.kpipes.lib.process.ProcessManager
import org.apache.commons.lang3.Validate

import static org.slf4j.LoggerFactory.getLogger
import static ServiceStartupResults.alreadyRunning
import static net.kpipes.lib.process.Command.cmd

class CommandLineDocker implements Docker {

    private final static LOG = getLogger(CommandLineDocker.class)

    private final static MAPPER = new ObjectMapper()

    private final ProcessManager processManager

    // Constructor

    CommandLineDocker(ProcessManager processManager) {
        this.processManager = processManager
    }

    // Operations implementation

    @Override
    List<String> execute(Container container) {
        processManager.execute(cmd(buildRunCommand(container, false)))
    }

    ServiceStartupResults startService(Container container) {
        Validate.notNull(container, 'Container cannot be null.')
        LOG.debug('About to execute container service: {}', container)

        Validate.notBlank(container.name(), 'Container service name must not be empty.')

        switch(status(container.name())) {
            case ContainerStatus.running: return alreadyRunning
            case ContainerStatus.created:
                processManager.execute(cmd("docker start ${container.name()}"))
                return started
            case ContainerStatus.none:
                processManager.execute(cmd(buildRunCommand(container, true)))
                return ServiceStartupResults.created
        }
    }

    ContainerStatus status(String name) {
        if (processManager.execute(cmd("docker ps -a -f name=${name}")).size() > 1) {
            if (processManager.execute(cmd("docker ps -f name=${name}")).size() > 1) {
                ContainerStatus.running
            } else {
                ContainerStatus.created
            }
        } else {
            ContainerStatus.none
        }
    }

    @Override
    void stop(String name) {
        Validate.notBlank(name, 'Container name cannot be blank.')
        LOG.debug('About to stop container: {}', name)

        processManager.execute(cmd("docker stop ${name}"))
    }

    @Override
    InspectResults inspect(String containerId) {
        def commandOutput = processManager.execute(cmd("docker inspect ${containerId}")).join(' ')
        def trimmedCommandOutput = commandOutput.substring(1, commandOutput.length() - 1)
        new InspectResults(MAPPER.readValue(trimmedCommandOutput, Map.class))
    }

    // Helpers

    static private String buildRunCommand(Container container, boolean daemon) {
        def command = 'docker run'
        if(daemon) {
            command += ' -d'
        } else {
            if (container.cleanUp()) {
                command += ' --rm'
            }
        }
        if(container.name() != null) {
            command += " --name=${container.name()}"
        }
        if(container.net() != null) {
            command += " --net=${container.net()} "
        }
        command += " ${container.volumes().inject('') { volumes, volume -> "${volumes} -v ${volume.key}:${volume.value}"}}"
        command += " ${container.environment().inject('') { environment, variable -> "${environment} -e ${variable.key}=${variable.value}"}}"
        command + " -t ${container.image()} ${container.arguments().join(' ')}"
    }

}
