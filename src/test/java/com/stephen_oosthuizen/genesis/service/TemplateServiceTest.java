package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.exception.TemplateNotFoundException;
import com.stephen_oosthuizen.genesis.model.*;
import freemarker.template.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TemplateService
 * Tests template rendering, variable substitution, and error handling
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.freemarker.cache=false" // Disable cache for testing
})
class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    private TemplateContext context;
    private ProjectConfiguration config;

    @BeforeEach
    void setUp() {
        // Create test project configuration
        Component flask = Component.builder()
                .id("flask")
                .name("Flask")
                .description("Web framework")
                .type(ComponentType.WEB_FRAMEWORK)
                .dependencies(List.of("Flask==3.0.0"))
                .build();

        Component postgresql = Component.builder()
                .id("postgresql")
                .name("PostgreSQL")
                .description("Database")
                .type(ComponentType.DATABASE)
                .dependencies(List.of("psycopg2-binary==2.9.9"))
                .build();

        Set<Component> components = new HashSet<>();
        components.add(flask);
        components.add(postgresql);

        config = ProjectConfiguration.builder()
                .projectName("test_project")
                .description("Test project description")
                .pythonVersion("3.11")
                .authorName("Test Author")
                .authorEmail("test@example.com")
                .components(components)
                .build();

        context = new TemplateContext(config);
    }

    @Test
    void shouldRenderRequirementsTxtTemplate() {
        // When
        String result = templateService.renderTemplate("base/requirements.txt.ftl", context);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("test_project");
        assertThat(result).contains("Flask==3.0.0");
        assertThat(result).contains("psycopg2-binary==2.9.9");
        assertThat(result).contains("python-dotenv==1.0.0");
    }

    @Test
    void shouldRenderReadmeTemplate() {
        // When
        String result = templateService.renderTemplate("base/README.md.ftl", context);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("# test_project");
        assertThat(result).contains("Test project description");
        assertThat(result).contains("Flask");
        assertThat(result).contains("PostgreSQL");
        assertThat(result).contains("Python Version: 3.11");
    }

    @Test
    void shouldRenderFlaskAppTemplate() {
        // When
        String result = templateService.renderTemplate("web/flask/app.py.ftl", context);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("Flask application factory");
        assertThat(result).contains("test_project");
        assertThat(result).contains("create_app()");
        assertThat(result).contains("CORS(app)");
    }

    @Test
    void shouldSubstituteVariablesCorrectly() {
        // When
        String result = templateService.renderTemplate("base/main.py.ftl", context);

        // Then
        assertThat(result).contains("test_project");
        assertThat(result).contains("Test project description");
        assertThat(result).contains("Config()");
    }

    @Test
    void shouldHandleConditionalLogic() {
        // When - with database
        String resultWithDb = templateService.renderTemplate("base/main.py.ftl", context);

        // Then - should include database imports
        assertThat(resultWithDb).contains("Database modules available");

        // When - without database
        config.setComponents(new HashSet<>());
        context = new TemplateContext(config);
        String resultWithoutDb = templateService.renderTemplate("base/main.py.ftl", context);

        // Then - should not include database imports
        assertThat(resultWithoutDb).doesNotContain("db_connection");
    }

    @Test
    void shouldThrowExceptionForMissingTemplate() {
        // When & Then
        assertThatThrownBy(() ->
                templateService.renderTemplate("nonexistent/template.ftl", context)
        )
                .isInstanceOf(TemplateNotFoundException.class)
                .hasMessageContaining("Template not found: nonexistent/template.ftl");
    }

    @Test
    void shouldCheckIfTemplateExists() {
        // When & Then
        assertThat(templateService.templateExists("base/requirements.txt.ftl")).isTrue();
        assertThat(templateService.templateExists("base/README.md.ftl")).isTrue();
        assertThat(templateService.templateExists("nonexistent.ftl")).isFalse();
    }

    @Test
    void shouldHandleEmptyComponents() {
        // Given - config with no components
        config.setComponents(new HashSet<>());
        context = new TemplateContext(config);

        // When
        String result = templateService.renderTemplate("base/requirements.txt.ftl", context);

        // Then - should still render successfully with base dependencies
        assertThat(result).isNotNull();
        assertThat(result).contains("python-dotenv==1.0.0");
    }

    @Test
    void shouldHandleMultipleDatabases() {
        // Given - multiple databases
        Component postgresql = Component.builder()
                .id("postgresql")
                .name("PostgreSQL")
                .type(ComponentType.DATABASE)
                .dependencies(List.of("psycopg2-binary==2.9.9"))
                .build();

        Component redis = Component.builder()
                .id("redis")
                .name("Redis")
                .type(ComponentType.DATABASE)
                .dependencies(List.of("redis==5.0.1"))
                .build();

        Set<Component> components = new HashSet<>();
        components.add(postgresql);
        components.add(redis);
        config.setComponents(components);
        context = new TemplateContext(config);

        // When
        String result = templateService.renderTemplate("base/requirements.txt.ftl", context);

        // Then - should include both
        assertThat(result).contains("psycopg2-binary==2.9.9");
        assertThat(result).contains("redis==5.0.1");
    }
}
