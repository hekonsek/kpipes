package net.kpipes.function.view.materialize.keyvalue.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.unit.TestContext
import net.kpipes.core.PipeBuilder
import net.kpipes.lib.testing.KPipesTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test

import static net.kpipes.core.KPipesFactory.kpipes
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore
import static org.assertj.core.api.Assertions.assertThat

class MaterializeKeyValueViewFunctionTest extends KPipesTest {

    PipeBuilder pipeBuilder

    @Before
    void before() {
        kpipes = kpipes()
        pipeBuilder = kpipes.pipeBuilder()
    }

    // Tests

    @Test
    void shouldMaterializeTopicAsView() {
        // Given
        pipeBuilder.build(tenant, "${source} | view.materialize.keyvalue")
        kpipes.start()

        // When
        kafkaProducer.send(new ProducerRecord(effectiveSource, key, new Bytes(new ObjectMapper().writeValueAsBytes([foo: 'baz']))))
        Thread.sleep(1000)

        // Then
        def savedBinaries = pipeBuilder.kafkaStreams().store(effectiveSource, keyValueStore()).get(key) as Bytes
        def savedValue = new ObjectMapper().readValue(savedBinaries.get(), Map).foo as String
        assertThat(savedValue).isEqualTo('baz')
    }

}