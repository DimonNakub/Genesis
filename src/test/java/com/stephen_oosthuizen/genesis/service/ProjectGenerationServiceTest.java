package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.dto.ProjectRequest;
import com.stephen_oosthuizen.genesis.exception.ComponentNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ProjectGenerationService
 * Tests the orchestration of project generation
 */
@SpringBootTest
class ProjectGenerationServiceTest {

    @Autowired
    private ProjectGenerationService generationService;

    @Test
    void shouldGenerateProjectSuccessfully() {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_project")
                .description("Test description")
                .pythonVersion("3.11")
                .authorName("Test Author")
                .components(List.of("flask", "postgresql"))
                .build();

        // When
        byte[] result = generationService.generateProject(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(1000); // Should be a substantial ZIP file
    }

    @Test
    void shouldThrowExceptionForInvalidComponent() {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_project")
                .pythonVersion("3.11")
                .components(List.of("invalid_component"))
                .build();

        // When & Then
        assertThatThrownBy(() -> generationService.generateProject(request))
                .isInstanceOf(ComponentNotFoundException.class)
                .hasMessageContaining("invalid_component");
    }

    @Test
    void shouldGenerateProjectWithMultipleComponents() {
        // Given - complex configuration
        ProjectRequest request = ProjectRequest.builder()
                .projectName("complex_app")
                .pythonVersion("3.11")
                .components(List.of("fastapi", "postgresql", "redis", "jwt", "docker", "pytest"))
                .build();

        // When
        byte[] result = generationService.generateProject(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(5000); // Larger project with more components
    }

    @Test
    void shouldGenerateProjectWithMinimalInfo() {
        // Given - only project name
        ProjectRequest request = ProjectRequest.builder()
                .projectName("minimal")
                .pythonVersion("3.11")
                .components(List.of())
                .build();

        // When
        byte[] result = generationService.generateProject(request);

        // Then - should succeed
        assertThat(result).isNotNull();
    }

    @Test
    void shouldHandleAllDatabaseTypes() {
        String[] databases = {"postgresql", "mysql", "mongodb", "sqlite", "redis"};

        for (String database : databases) {
            // Given
            ProjectRequest request = ProjectRequest.builder()
                    .projectName("db_test")
                    .pythonVersion("3.11")
                    .components(List.of("flask", database))
                    .build();

            // When & Then - should succeed for all database types
            assertThatCode(() -> generationService.generateProject(request))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void shouldHandleAllWebFrameworks() {
        String[] frameworks = {"flask", "fastapi", "django"};

        for (String framework : frameworks) {
            // Given
            ProjectRequest request = ProjectRequest.builder()
                    .projectName("web_test")
                    .pythonVersion("3.11")
                    .components(List.of(framework))
                    .build();

            // When & Then - should succeed for all frameworks
            assertThatCode(() -> generationService.generateProject(request))
                    .doesNotThrowAnyException();
        }
    }
}
