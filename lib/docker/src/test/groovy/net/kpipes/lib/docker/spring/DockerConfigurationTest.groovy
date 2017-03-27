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
package net.kpipes.lib.docker.spring

import net.kpipes.lib.docker.ContainerBuilder
import net.kpipes.lib.docker.ContainerStatus
import net.kpipes.lib.docker.Docker
import net.kpipes.lib.process.spring.ProcessManagerConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static net.kpipes.lib.docker.Container.container
import static net.kpipes.lib.docker.ServiceStartupResults.alreadyRunning
import static net.kpipes.lib.docker.ServiceStartupResults.created
import static org.assertj.core.api.Assertions.assertThat
import static net.kpipes.lib.commons.Uuids.uuid

@RunWith(SpringRunner)
@SpringBootTest(classes = [ProcessManagerConfiguration, DockerConfiguration])
class DockerConfigurationTest {

    // Fixtures

    @Autowired
    Docker docker

    def containerName = uuid()

    // Tests

    @Test
    void shouldCreateContainer() {
        def status = docker.startService(container('ubuntu', containerName))
        assertThat(status).isEqualTo(created)
    }

    @Test
    void shouldStartCreatedContainer() {
        def startupStatus = docker.startService(new ContainerBuilder('ubuntu').name(containerName).net('host').arguments('top').build())
        def containerStatus = docker.status(containerName)
        assertThat(startupStatus).isEqualTo(created)
        assertThat(containerStatus).isEqualTo(ContainerStatus.running)
    }

    @Test
    void shouldNotStartContainerSecondTime() {
        def container = new ContainerBuilder('ubuntu').name(containerName).build()
        docker.startService(container)
        def status = docker.startService(container)
        assertThat(status).isEqualTo(alreadyRunning)
    }

    @Test
    void shouldStopContainer() {
        // Given
        docker.startService(new ContainerBuilder('ubuntu').name(containerName).arguments('top').build())

        // When
        docker.stop(containerName)

        // Then
        def containerStatus = docker.status(containerName)
        assertThat(containerStatus).isEqualTo(ContainerStatus.created)
    }

    @Test
    void shouldInspectContainer() {
        // Given
        docker.startService(new ContainerBuilder('ubuntu').name(containerName).arguments('top').build())

        // When
        def inspectResults = docker.inspect(containerName)

        // Then
        assertThat(inspectResults.environment()).isNotNull()
    }

    @Test
    void shouldSetEnvironmentOnContainer() {
        // Given
        docker.startService(new ContainerBuilder('ubuntu').name(containerName).environment([foo: 'bar']).arguments('top').build())

        // When
        def inspectResults = docker.inspect(containerName)

        // Then
        assertThat(inspectResults.environment()).containsEntry('foo', 'bar')
    }

}
