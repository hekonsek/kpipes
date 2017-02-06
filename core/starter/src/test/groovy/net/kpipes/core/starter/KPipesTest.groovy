package net.kpipes.core.starter

import net.kpipes.core.starter.spi.Service
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class KPipesTest {

    static kpipes = new KPipes().start()

    @Test
    void shouldRetrieveService() {
        // Given
        kpipes.serviceRegistry().registerService('foo')

        // When
        def stringService = kpipes.service(String)

        // Then
        assertThat(stringService).isEqualTo('foo')
    }

    @Test
    void shouldRetrieveServiceFromFactory() {
        // When
        def stringService = kpipes.service(Date)

        // Then
        assertThat(stringService).isNotNull()
    }

    @Test
    void shouldExecuteStartCallback() {
        // When
        def service = kpipes.service(ServiceClass)

        // Then
        assertThat(service.started).isTrue()
    }

    // Fixture classes

    static class ServiceFactories {

        @Service
        Date date(KPipes kpipes) {
            assertThat(kpipes).isNotNull()
            new Date()
        }

        @Service(onStart = 'start')
        def serviceClass(KPipes kpipes) {
            new ServiceClass()
        }

    }

    static class ServiceClass {

        boolean started

        void start() {
            started = true
        }

    }

}
