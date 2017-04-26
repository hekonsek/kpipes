package net.kpipes.function.machinelearning.predict.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.kpipes.core.PipeBuilder
import net.kpipes.core.spring.KPipesFactory
import net.kpipes.lib.kafka.client.KafkaConsumerBuilder
import net.kpipes.lib.kafka.client.executor.KafkaConsumerTemplate
import net.kpipes.lib.testing.KPipesTest
import net.kpipes.service.machinelearning.MachineLearningService
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static net.kpipes.lib.commons.Uuids.uuid
import static org.assertj.core.api.Assertions.assertThat

@RunWith(VertxUnitRunner)
class MachineLearningPredictFunctionTest extends KPipesTest {

    PipeBuilder pipeBuilder

    def home = Files.createTempDir()

    @Before
    void before() {
        System.setProperty('kpipes.home', home.absolutePath)
        System.setProperty('applicationId', uuid())
        System.setProperty('nodeId', uuid())
        kpipes = KPipesFactory.kpipes()
        pipeBuilder = kpipes.pipeBuilder()
    }

    // Tests

    @Test
    void shouldPredict(TestContext context) {
        // Given
        def async = context.async()
        def dataset = uuid()
        pipeBuilder.build(tenant, "${dataset} | view.materialize.keyvalue")
        pipeBuilder.build(tenant, "${source} | machinelearning.predict [model: 'mymodel'] | ${target}")
        kpipes.startPipes()

        send("${tenant}.${dataset}", 'key1', [vector: [0,0,0], label: 'small'])
        send("${tenant}.${dataset}", 'key2', [vector: [100,100,100], label: 'big'])
        Thread.sleep(2000)
        kpipes.serviceRegistry().service(MachineLearningService).train(tenant, dataset, 'mymodel')

        // When
        kafkaProducer.send(new ProducerRecord(effectiveSource, key, new Bytes(new ObjectMapper().writeValueAsBytes([featureVector:[vector: [150,150,150]]]))))

        // Then
        kpipes.serviceRegistry().service(KafkaConsumerTemplate).subscribe(new KafkaConsumerBuilder<>(uuid()).port(kafkaPort).build(), effectiveTarget) {
            def response = json.read(it.value() as Bytes)
            def prediction = response['response.machinelearning.predict'] as String
            assertThat(prediction).isEqualTo('big')
            async.complete()
        }
    }

}