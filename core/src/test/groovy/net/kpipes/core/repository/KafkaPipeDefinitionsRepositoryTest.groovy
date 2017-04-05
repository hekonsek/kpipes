package net.kpipes.core.repository

import net.kpipes.core.KPipes
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static net.kpipes.core.PipeDefinitionEncoder.decodePipe
import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class KafkaPipeDefinitionsRepositoryTest {

    static KPipes kpipes

    static {
        System.setProperty('kafka.broker.enabled', 'false')
        System.setProperty('kpipes.home', createTempDir().absolutePath)
        System.setProperty('applicationId', uuid())
        System.setProperty('nodeId', uuid())
        kpipes = kpipes().startPipes()
    }

    @Test
    void shouldListAddedPipe() {
        // Given
        def repository = kpipes.serviceRegistry().service(PipeDefinitionsRepository)
        repository.add(decodePipe('tenant', 'foo | bar [config: "entry"] | baz'))

        // When
        Thread.sleep(1000)
        def pipes = repository.list()

        // Then
        assertThat(pipes.find{ it.from() == 'foo' }).isNotNull()
    }

    @Test
    void shouldRemovePipe() {
        // Given
        def repository = kpipes.serviceRegistry().service(PipeDefinitionsRepository)
        def pipe = decodePipe('tenant', 'bar | bar [config: "entry"] | baz')
        repository.add(pipe)
        Thread.sleep(1000)

        // When
        repository.remove(pipe.id())

        // Then
        Thread.sleep(1000)
        def pipes = repository.list()
        assertThat(pipes.find{ it.from() == 'bar' }).isNull()
    }

}