package com.stephen_oosthuizen.genesis.metadata;

import com.stephen_oosthuizen.genesis.model.Component;
import com.stephen_oosthuizen.genesis.model.ComponentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for YamlMetadataLoader
 * Tests YAML parsing and component loading
 */
@SpringBootTest
class YamlMetadataLoaderTest {

    @Autowired
    private YamlMetadataLoader yamlLoader;

    @Test
    void shouldLoadComponentsFromYaml() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).isNotNull();
        assertThat(components).isNotEmpty();
        assertThat(components).hasSize(18); // All 18 components
    }

    @Test
    void shouldLoadFlaskComponent() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).containsKey("flask");

        Component flask = components.get("flask");
        assertThat(flask.getId()).isEqualTo("flask");
        assertThat(flask.getName()).isEqualTo("Flask");
        assertThat(flask.getType()).isEqualTo(ComponentType.WEB_FRAMEWORK);
        assertThat(flask.getDependencies()).contains("Flask==3.0.0", "Flask-CORS==4.0.0");
        assertThat(flask.getConflicts()).contains("django", "fastapi");
    }

    @Test
    void shouldLoadDatabaseComponents() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).containsKeys("postgresql", "mysql", "mongodb", "sqlite", "redis");

        // Verify PostgreSQL
        Component postgresql = components.get("postgresql");
        assertThat(postgresql.getType()).isEqualTo(ComponentType.DATABASE);
        assertThat(postgresql.getDependencies()).contains("psycopg2-binary==2.9.9");
        assertThat(postgresql.getTemplatePaths()).contains("database/postgresql/db_connection.py.ftl");
        assertThat(postgresql.getDocumentationPaths()).contains("database/postgresql/DB_USAGE_INSTRUCTIONS.md.ftl");
    }

    @Test
    void shouldLoadSecurityComponents() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).containsKeys("jwt", "oauth2", "basicauth", "apikey");

        // Verify JWT
        Component jwt = components.get("jwt");
        assertThat(jwt.getType()).isEqualTo(ComponentType.SECURITY);
        assertThat(jwt.getRequires()).containsAnyOf("flask", "fastapi", "django");
        assertThat(jwt.getDependencies()).contains("PyJWT==2.8.0");
    }

    @Test
    void shouldLoadGuiComponents() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).containsKeys("tkinter", "pyqt5", "streamlit");

        // Verify Tkinter
        Component tkinter = components.get("tkinter");
        assertThat(tkinter.getType()).isEqualTo(ComponentType.GUI);
        assertThat(tkinter.getConflicts()).contains("flask", "fastapi", "django");
    }

    @Test
    void shouldLoadTestingComponents() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).containsKeys("pytest", "unittest");

        Component pytest = components.get("pytest");
        assertThat(pytest.getType()).isEqualTo(ComponentType.TESTING);
        assertThat(pytest.getDependencies()).contains("pytest==7.4.4");
    }

    @Test
    void shouldLoadDockerComponent() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        assertThat(components).containsKey("docker");

        Component docker = components.get("docker");
        assertThat(docker.getType()).isEqualTo(ComponentType.DOCKER);
        assertThat(docker.getTemplatePaths()).contains("docker/Dockerfile.ftl", "docker/docker-compose.yml.ftl");
    }

    @Test
    void shouldHandleComponentsWithNoConflicts() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        Component pytest = components.get("pytest");
        assertThat(pytest.getConflicts()).isEmpty();

        Component redis = components.get("redis");
        assertThat(redis.getConflicts()).isEmpty();
    }

    @Test
    void shouldHandleComponentsWithNoDependencies() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        Component tkinter = components.get("tkinter");
        assertThat(tkinter.getDependencies()).isEmpty();

        Component basicauth = components.get("basicauth");
        assertThat(basicauth.getDependencies()).isEmpty();
    }

    @Test
    void shouldLoadAllExpectedComponentTypes() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then - verify we have all component types
        Set<ComponentType> types = new HashSet<>();
        components.values().forEach(c -> types.add(c.getType()));

        assertThat(types).contains(
                ComponentType.DATABASE,
                ComponentType.WEB_FRAMEWORK,
                ComponentType.SECURITY,
                ComponentType.GUI,
                ComponentType.TESTING,
                ComponentType.DOCKER
        );
    }

    @Test
    void shouldLoadTemplatePathsCorrectly() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        Component fastapi = components.get("fastapi");
        assertThat(fastapi.getTemplatePaths())
                .contains("web/fastapi/main.py.ftl")
                .contains("web/fastapi/routers.py.ftl");

        Component django = components.get("django");
        assertThat(django.getTemplatePaths())
                .contains("web/django/manage.py.ftl")
                .contains("web/django/settings.py.ftl")
                .contains("web/django/urls.py.ftl");
    }

    @Test
    void shouldLoadDocumentationPaths() {
        // When
        Map<String, Component> components = yamlLoader.loadComponents();

        // Then
        Component postgresql = components.get("postgresql");
        assertThat(postgresql.getDocumentationPaths())
                .contains("database/postgresql/DB_USAGE_INSTRUCTIONS.md.ftl");

        Component jwt = components.get("jwt");
        assertThat(jwt.getDocumentationPaths())
                .contains("security/jwt/SECURITY_README.md.ftl");
    }
}
