package net.kpipes.core.starter

import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class KPipesTest {

    @Test
    void shouldRetrieveService() {
        // Given
        def kpipes = new KPipes().start()
        kpipes.serviceRegistry().registerService('foo')

        // When
        def stringService = kpipes.service(String)

        // Then
        assertThat(stringService).isEqualTo('foo')
    }

    @Test
    void shouldRetrieveServiceFromFactory() {
        // Given
        def kpipes = new KPipes().start()

        // When
        def stringService = kpipes.service(Date)

        // Then
        assertThat(stringService).isNotNull()
    }

    static class DateFactory {

        @Service
        Date date(KPipes kpipes) {
            assertThat(kpipes).isNotNull()
            new Date()
        }

    }

}
