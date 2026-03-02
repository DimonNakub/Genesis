package com.stephen_oosthuizen.genesis.metadata;

import com.stephen_oosthuizen.genesis.dto.ProjectMetadata;
import com.stephen_oosthuizen.genesis.model.Component;
import com.stephen_oosthuizen.genesis.model.ComponentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ComponentRegistry
 * Tests component lookup, metadata generation, and registry operations
 */
@SpringBootTest
class ComponentRegistryTest {

    @Autowired
    private ComponentRegistry componentRegistry;

    @Test
    void shouldLoadAllComponents() {
        // When
        var allComponents = componentRegistry.getAllComponents();

        // Then
        assertThat(allComponents).hasSize(18);
    }

    @Test
    void shouldFindComponentById() {
        // When
        Optional<Component> flask = componentRegistry.getComponent("flask");
        Optional<Component> postgresql = componentRegistry.getComponent("postgresql");

        // Then
        assertThat(flask).isPresent();
        assertThat(flask.get().getName()).isEqualTo("Flask");

        assertThat(postgresql).isPresent();
        assertThat(postgresql.get().getName()).isEqualTo("PostgreSQL");
    }

    @Test
    void shouldReturnEmptyForNonExistentComponent() {
        // When
        Optional<Component> component = componentRegistry.getComponent("nonexistent");

        // Then
        assertThat(component).isEmpty();
    }

    @Test
    void shouldCheckIfComponentExists() {
        // When & Then
        assertThat(componentRegistry.hasComponent("flask")).isTrue();
        assertThat(componentRegistry.hasComponent("postgresql")).isTrue();
        assertThat(componentRegistry.hasComponent("nonexistent")).isFalse();
    }

    @Test
    void shouldGetComponentsByType() {
        // When
        List<Component> webFrameworks = componentRegistry.getComponentsByType(ComponentType.WEB_FRAMEWORK);
        List<Component> databases = componentRegistry.getComponentsByType(ComponentType.DATABASE);
        List<Component> security = componentRegistry.getComponentsByType(ComponentType.SECURITY);

        // Then
        assertThat(webFrameworks).hasSize(3); // Flask, FastAPI, Django
        assertThat(databases).hasSize(5); // PostgreSQL, MySQL, MongoDB, SQLite, Redis
        assertThat(security).hasSize(4); // JWT, OAuth2, BasicAuth, APIKey

        assertThat(webFrameworks).extracting("id")
                .containsExactlyInAnyOrder("flask", "fastapi", "django");

        assertThat(databases).extracting("id")
                .containsExactlyInAnyOrder("postgresql", "mysql", "mongodb", "sqlite", "redis");
    }

    @Test
    void shouldGenerateProjectMetadata() {
        // When
        ProjectMetadata metadata = componentRegistry.getMetadata();

        // Then
        assertThat(metadata).isNotNull();
        assertThat(metadata.getVersion()).isEqualTo("1.0.0");
        assertThat(metadata.getDefaultPythonVersion()).isEqualTo("3.11");
        assertThat(metadata.getSupportedPythonVersions()).contains("3.9", "3.10", "3.11", "3.12");
        assertThat(metadata.getComponents()).hasSize(18);
        assertThat(metadata.getTimestamp()).isNotNull();
    }

    @Test
    void shouldIncludeAllComponentTypesInMetadata() {
        // When
        ProjectMetadata metadata = componentRegistry.getMetadata();

        // Then - verify all types are represented
        var componentTypes = metadata.getComponents().stream()
                .map(c -> c.getType())
                .distinct()
                .toList();

        assertThat(componentTypes).contains(
                ComponentType.DATABASE,
                ComponentType.WEB_FRAMEWORK,
                ComponentType.SECURITY,
                ComponentType.GUI,
                ComponentType.TESTING,
                ComponentType.DOCKER
        );
    }

    @Test
    void shouldIncludeConflictsInMetadata() {
        // When
        ProjectMetadata metadata = componentRegistry.getMetadata();

        // Then
        var flaskOption = metadata.getComponents().stream()
                .filter(c -> c.getId().equals("flask"))
                .findFirst()
                .orElseThrow();

        assertThat(flaskOption.getConflicts()).contains("django", "fastapi");
    }

    @Test
    void shouldIncludeRequirementsInMetadata() {
        // When
        ProjectMetadata metadata = componentRegistry.getMetadata();

        // Then
        var jwtOption = metadata.getComponents().stream()
                .filter(c -> c.getId().equals("jwt"))
                .findFirst()
                .orElseThrow();

        assertThat(jwtOption.getRequires()).containsAnyOf("flask", "fastapi", "django");
    }
}
