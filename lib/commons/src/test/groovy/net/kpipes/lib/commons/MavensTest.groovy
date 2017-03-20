package net.kpipes.lib.commons

import org.assertj.core.api.Assertions
import org.junit.Test

import static net.kpipes.lib.commons.Mavens.MavenCoordinates.parseMavenCoordinates
import static net.kpipes.lib.commons.Mavens.artifactVersionFromDependenciesProperties

class MavensTest {

    @Test
    void shouldParseCoordinates() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}:${artifact}:${version}"

        // When
        def coordinates = parseMavenCoordinates(coordinatesString)

        // Then
        Assertions.assertThat(coordinates.artifactId).isEqualTo(artifact)
        Assertions.assertThat(coordinates.groupId).isEqualTo(group)
        Assertions.assertThat(coordinates.version).isEqualTo(version)
    }

    @Test
    void shouldParseCoordinatesWithCustomSeparator() {
        // Given
        def group = 'foo'
        def artifact = 'bar'
        def version = '1'
        def coordinatesString = "${group}/${artifact}/${version}"

        // When
        def coordinates = parseMavenCoordinates(coordinatesString, '/')

        // Then
        Assertions.assertThat(coordinates.artifactId).isEqualTo(artifact)
        Assertions.assertThat(coordinates.groupId).isEqualTo(group)
        Assertions.assertThat(coordinates.version).isEqualTo(version)
    }

    @Test
    void shouldLoadDependencyVersion() {
        def version = artifactVersionFromDependenciesProperties('com.test', 'test')
        Assertions.assertThat(version).contains('6.6.6')
    }

    @Test
    void shouldHandleMissingDependency() {
        def version = artifactVersionFromDependenciesProperties('invalidGroupId', 'invalidArtifacts')
        Assertions.assertThat(version).isEmpty()
    }

}