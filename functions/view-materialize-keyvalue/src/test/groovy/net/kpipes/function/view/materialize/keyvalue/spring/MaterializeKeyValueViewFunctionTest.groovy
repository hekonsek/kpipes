package net.kpipes.function.view.materialize.keyvalue.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import net.kpipes.core.PipeBuilder
import net.kpipes.core.store.FileSystemKeyValueStore
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test

import static net.kpipes.core.spring.KPipesFactory.kpipes
import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

class MaterializeKeyValueViewFunctionTest extends KPipesTest {

    PipeBuilder pipeBuilder

    def home = Files.createTempDir()

    @Before
    void before() {
        System.setProperty('kpipes.home', home.absolutePath)
        System.setProperty('applicationId', uuid())
        System.setProperty('nodeId', uuid())
        kpipes = kpipes()
        pipeBuilder = kpipes.pipeBuilder()
    }

    // Tests

    @Test
    void shouldMaterializeTopicAsView() {
        // Given
        pipeBuilder.build(tenant, "${source} | view.materialize.keyvalue")
        kpipes.startPipes()

        // When
        kafkaProducer.send(new ProducerRecord(effectiveSource, key, new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'baz']))))
        Thread.sleep(1000)

        // Then
        def savedBinaries = kpipes.@serviceRegistry.service(FileSystemKeyValueStore).read(effectiveSource, key)
        def savedValue = new ObjectMapper().readValue(savedBinaries, Map).foo as String
        assertThat(savedValue).isEqualTo('baz')
    }

}