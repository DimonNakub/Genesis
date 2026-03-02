package com.stephen_oosthuizen.genesis.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephen_oosthuizen.genesis.dto.ProjectRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for project generation API
 * Tests the complete flow from HTTP request to ZIP response
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProjectGenerationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.application").value("Genesis Python Initializr"))
                .andExpect(jsonPath("$.componentsAvailable").value(18));
    }

    @Test
    void shouldReturnMetadata() throws Exception {
        mockMvc.perform(get("/api/v1/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.defaultPythonVersion").value("3.11"))
                .andExpect(jsonPath("$.components").isArray())
                .andExpect(jsonPath("$.components.length()").value(18));
    }

    @Test
    void shouldGenerateFlaskPostgreSQLProject() throws Exception {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_flask_app")
                .description("Test Flask application")
                .pythonVersion("3.11")
                .authorName("Test Author")
                .authorEmail("test@example.com")
                .components(List.of("flask", "postgresql"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/octet-stream"))
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        // Then - verify ZIP structure
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        assertThat(zipBytes.length).isGreaterThan(0);

        Map<String, String> files = extractZipContents(zipBytes);

        // Verify base files
        assertThat(files).containsKeys(
                "test_flask_app/README.md",
                "test_flask_app/requirements.txt",
                "test_flask_app/main.py",
                "test_flask_app/.gitignore",
                "test_flask_app/.env.template"
        );

        // Verify Flask files
        assertThat(files).containsKeys(
                "test_flask_app/app.py",
                "test_flask_app/routes.py"
        );

        // Verify PostgreSQL files
        assertThat(files).containsKey("test_flask_app/config/db_connection_postgresql.py");

        // Verify setup scripts
        assertThat(files).containsKeys(
                "test_flask_app/setup_venv.sh",
                "test_flask_app/setup_venv.bat",
                "test_flask_app/run_dev.sh",
                "test_flask_app/run_dev.bat"
        );

        // Verify content
        assertThat(files.get("test_flask_app/README.md")).contains("test_flask_app");
        assertThat(files.get("test_flask_app/requirements.txt")).contains("Flask==3.0.0");
        assertThat(files.get("test_flask_app/requirements.txt")).contains("psycopg2-binary==2.9.9");
    }

    @Test
    void shouldGenerateFastAPIMongoDBProject() throws Exception {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("fastapi_mongo")
                .pythonVersion("3.11")
                .components(List.of("fastapi", "mongodb", "jwt", "docker"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        // Verify FastAPI files
        assertThat(files).containsKey("fastapi_mongo/routers.py");

        // Verify MongoDB files
        assertThat(files).containsKey("fastapi_mongo/config/db_connection_mongodb.py");

        // Verify JWT files
        assertThat(files).containsKey("fastapi_mongo/config/jwt_handler.py");

        // Verify Docker files
        assertThat(files).containsKeys(
                "fastapi_mongo/Dockerfile",
                "fastapi_mongo/docker-compose.yml",
                "fastapi_mongo/.dockerignore"
        );

        // Verify requirements
        assertThat(files.get("fastapi_mongo/requirements.txt")).contains("fastapi==0.109.0");
        assertThat(files.get("fastapi_mongo/requirements.txt")).contains("pymongo==4.6.1");
        assertThat(files.get("fastapi_mongo/requirements.txt")).contains("PyJWT==2.8.0");
    }

    @Test
    void shouldGenerateMultipleDatabaseProject() throws Exception {
        // Given - PostgreSQL + Redis
        ProjectRequest request = ProjectRequest.builder()
                .projectName("multi_db_app")
                .pythonVersion("3.11")
                .components(List.of("flask", "postgresql", "redis", "pytest"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        // Verify both database connection files
        assertThat(files).containsKeys(
                "multi_db_app/config/db_connection_postgresql.py",
                "multi_db_app/config/db_connection_redis.py"
        );

        // Verify requirements include both
        String requirements = files.get("multi_db_app/requirements.txt");
        assertThat(requirements).contains("psycopg2-binary==2.9.9");
        assertThat(requirements).contains("redis==5.0.1");
    }

    @Test
    void shouldGenerateStreamlitDashboard() throws Exception {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("data_dashboard")
                .pythonVersion("3.11")
                .components(List.of("streamlit", "postgresql"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        // Verify Streamlit file
        assertThat(files).containsKey("data_dashboard/streamlit_app.py");
        assertThat(files.get("data_dashboard/streamlit_app.py")).contains("streamlit");
    }

    @Test
    void shouldRejectInvalidProjectName() throws Exception {
        // Given - invalid project name (uppercase)
        ProjectRequest request = ProjectRequest.builder()
                .projectName("InvalidName")
                .components(List.of("flask"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void shouldRejectInvalidPythonVersion() throws Exception {
        // Given - invalid Python version
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_app")
                .pythonVersion("2.7")
                .components(List.of("flask"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNonExistentComponent() throws Exception {
        // Given - non-existent component
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_app")
                .pythonVersion("3.11")
                .components(List.of("nonexistent_component"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Component Not Found"));
    }

    @Test
    void shouldRejectIncompatibleComponents() throws Exception {
        // Given - Flask + Django (conflicting)
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_app")
                .pythonVersion("3.11")
                .components(List.of("flask", "django"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Incompatible Components"));
    }

    @Test
    void shouldRejectSecurityWithoutWebFramework() throws Exception {
        // Given - JWT without web framework
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_app")
                .pythonVersion("3.11")
                .components(List.of("jwt"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Incompatible Components"));
    }

    @Test
    void shouldGenerateMinimalProject() throws Exception {
        // Given - no components
        ProjectRequest request = ProjectRequest.builder()
                .projectName("minimal_app")
                .pythonVersion("3.11")
                .components(List.of())
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then - should still generate base files
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        assertThat(files).containsKeys(
                "minimal_app/README.md",
                "minimal_app/main.py",
                "minimal_app/requirements.txt"
        );
    }

    @Test
    void shouldGenerateDjangoProject() throws Exception {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("django_app")
                .pythonVersion("3.11")
                .components(List.of("django", "postgresql"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        // Verify Django structure
        assertThat(files).containsKeys(
                "django_app/manage.py",
                "django_app/django_app/settings.py",
                "django_app/django_app/urls.py"
        );
    }

    @Test
    void shouldGenerateTkinterDesktopApp() throws Exception {
        // Given
        ProjectRequest request = ProjectRequest.builder()
                .projectName("desktop_app")
                .pythonVersion("3.11")
                .components(List.of("tkinter", "sqlite"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        // Verify GUI file
        assertThat(files).containsKey("desktop_app/gui_app.py");
        assertThat(files.get("desktop_app/gui_app.py")).contains("tkinter");
    }

    @Test
    void shouldRejectTkinterWithWebFramework() throws Exception {
        // Given - Tkinter + Flask (incompatible)
        ProjectRequest request = ProjectRequest.builder()
                .projectName("test_app")
                .pythonVersion("3.11")
                .components(List.of("tkinter", "flask"))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Incompatible Components"));
    }

    @Test
    void shouldAllowStreamlitWithWebFramework() throws Exception {
        // Given - Streamlit + FastAPI (compatible)
        ProjectRequest request = ProjectRequest.builder()
                .projectName("streamlit_api")
                .pythonVersion("3.11")
                .components(List.of("streamlit", "fastapi"))
                .build();

        // When & Then - should succeed
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGenerateProjectWithAllSecurityOptions() throws Exception {
        // Given - Flask with JWT
        ProjectRequest request = ProjectRequest.builder()
                .projectName("secure_app")
                .pythonVersion("3.11")
                .components(List.of("flask", "jwt"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        assertThat(files).containsKey("secure_app/config/jwt_handler.py");
        assertThat(files.get("secure_app/requirements.txt")).contains("PyJWT==2.8.0");
    }

    @Test
    void shouldIncludeDockerFilesWhenRequested() throws Exception {
        // Given - with Docker
        ProjectRequest request = ProjectRequest.builder()
                .projectName("docker_app")
                .pythonVersion("3.11")
                .components(List.of("flask", "postgresql", "docker"))
                .build();

        // When
        MvcResult result = mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] zipBytes = result.getResponse().getContentAsByteArray();
        Map<String, String> files = extractZipContents(zipBytes);

        assertThat(files).containsKeys(
                "docker_app/Dockerfile",
                "docker_app/docker-compose.yml",
                "docker_app/.dockerignore"
        );

        // Verify Dockerfile contains correct configuration
        assertThat(files.get("docker_app/Dockerfile")).contains("python:3.11");
        assertThat(files.get("docker_app/docker-compose.yml")).contains("postgres");
    }

    @Test
    void shouldHandleEmptyComponentsList() throws Exception {
        // Given - no components
        ProjectRequest request = ProjectRequest.builder()
                .projectName("empty_app")
                .pythonVersion("3.11")
                .components(List.of())
                .build();

        // When & Then - should succeed
        mockMvc.perform(post("/api/v1/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldValidateProjectNameFormat() throws Exception {
        // Test various invalid formats
        String[] invalidNames = {
                "My-Project",      // Dashes
                "MyProject",       // Uppercase
                "123project",      // Starts with number
                "my project",      // Spaces
                "my.project"       // Dots
        };

        for (String invalidName : invalidNames) {
            ProjectRequest request = ProjectRequest.builder()
                    .projectName(invalidName)
                    .pythonVersion("3.11")
                    .components(List.of())
                    .build();

            mockMvc.perform(post("/api/v1/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void shouldAcceptValidProjectNames() throws Exception {
        // Test various valid formats
        String[] validNames = {
                "my_project",
                "test123",
                "app_v2",
                "hello_world_app"
        };

        for (String validName : validNames) {
            ProjectRequest request = ProjectRequest.builder()
                    .projectName(validName)
                    .pythonVersion("3.11")
                    .components(List.of())
                    .build();

            mockMvc.perform(post("/api/v1/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    /**
     * Helper method to extract ZIP contents
     */
    private Map<String, String> extractZipContents(byte[] zipBytes) throws IOException {
        Map<String, String> contents = new HashMap<>();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
             ZipArchiveInputStream zis = new ZipArchiveInputStream(bais)) {

            ZipArchiveEntry entry;
            while ((entry = zis.getNextZipEntry()) != null) {
                if (!entry.isDirectory()) {
                    byte[] content = zis.readAllBytes();
                    contents.put(entry.getName(), new String(content, StandardCharsets.UTF_8));
                }
            }
        }

        return contents;
    }
}
