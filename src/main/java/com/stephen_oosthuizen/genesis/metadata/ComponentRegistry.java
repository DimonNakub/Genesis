package com.stephen_oosthuizen.genesis.metadata;

import com.stephen_oosthuizen.genesis.dto.ComponentOption;
import com.stephen_oosthuizen.genesis.dto.ProjectMetadata;
import com.stephen_oosthuizen.genesis.model.Component;
import com.stephen_oosthuizen.genesis.model.ComponentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry of all available components.
 * Loads components from YAML configuration file.
 */
@Service
@Slf4j
public class ComponentRegistry {
    private final Map<String, Component> components;
    private final YamlMetadataLoader yamlLoader;

    public ComponentRegistry(YamlMetadataLoader yamlLoader) {
        this.yamlLoader = yamlLoader;
        log.info("Initializing ComponentRegistry from YAML");
        this.components = loadComponentsFromYaml();
    }

    /**
     * Load components from YAML file
     */
    private Map<String, Component> loadComponentsFromYaml() {
        try {
            Map<String, Component> loadedComponents = yamlLoader.loadComponents();
            log.info("Successfully loaded {} components from YAML", loadedComponents.size());
            return loadedComponents;
        } catch (Exception e) {
            log.error("Failed to load components from YAML, falling back to hardcoded components", e);
            return initializeHardcodedComponents();
        }
    }

    /**
     * Fallback: Initialize hardcoded components if YAML loading fails
     */
    private Map<String, Component> initializeHardcodedComponents() {
        log.warn("Using hardcoded component definitions");
        Map<String, Component> hardcodedComponents = new HashMap<>();

        // Keep the existing hardcoded initialization as fallback
        // ========== WEB FRAMEWORKS ==========

        // Flask
        Component flask = Component.builder()
                .id("flask")
                .name("Flask")
                .description("Lightweight WSGI web framework for Python")
                .type(ComponentType.WEB_FRAMEWORK)
                .dependencies(List.of(
                        "Flask==3.0.0",
                        "Flask-CORS==4.0.0"
                ))
                .templatePaths(List.of(
                        "web/flask/app.py.ftl",
                        "web/flask/routes.py.ftl"
                ))
                .documentationPaths(List.of())
                .conflicts(Set.of("django", "fastapi"))
                .build();

        // FastAPI
        Component fastapi = Component.builder()
                .id("fastapi")
                .name("FastAPI")
                .description("Modern async web framework with automatic API documentation")
                .type(ComponentType.WEB_FRAMEWORK)
                .dependencies(List.of(
                        "fastapi==0.109.0",
                        "uvicorn[standard]==0.27.0",
                        "pydantic==2.5.0"
                ))
                .templatePaths(List.of(
                        "web/fastapi/main.py.ftl",
                        "web/fastapi/routers.py.ftl"
                ))
                .documentationPaths(List.of())
                .conflicts(Set.of("flask", "django"))
                .build();

        // Django
        Component django = Component.builder()
                .id("django")
                .name("Django")
                .description("High-level web framework with batteries included")
                .type(ComponentType.WEB_FRAMEWORK)
                .dependencies(List.of(
                        "Django==5.0.0",
                        "djangorestframework==3.14.0"
                ))
                .templatePaths(List.of(
                        "web/django/manage.py.ftl",
                        "web/django/settings.py.ftl",
                        "web/django/urls.py.ftl"
                ))
                .documentationPaths(List.of())
                .conflicts(Set.of("flask", "fastapi"))
                .build();

        // ========== DATABASES ==========

        // PostgreSQL
        Component postgresql = Component.builder()
                .id("postgresql")
                .name("PostgreSQL")
                .description("Powerful open-source relational database")
                .type(ComponentType.DATABASE)
                .dependencies(List.of(
                        "psycopg2-binary==2.9.9",
                        "SQLAlchemy==2.0.25"
                ))
                .templatePaths(List.of(
                        "database/postgresql/db_connection.py.ftl"
                ))
                .documentationPaths(List.of(
                        "database/postgresql/DB_USAGE_INSTRUCTIONS.md.ftl"
                ))
                .conflicts(Set.of("sqlite"))
                .build();

        // MySQL
        Component mysql = Component.builder()
                .id("mysql")
                .name("MySQL")
                .description("Popular open-source relational database")
                .type(ComponentType.DATABASE)
                .dependencies(List.of(
                        "pymysql==1.1.0",
                        "SQLAlchemy==2.0.25"
                ))
                .templatePaths(List.of(
                        "database/mysql/db_connection.py.ftl"
                ))
                .documentationPaths(List.of(
                        "database/mysql/DB_USAGE_INSTRUCTIONS.md.ftl"
                ))
                .conflicts(Set.of("sqlite"))
                .build();

        // MongoDB
        Component mongodb = Component.builder()
                .id("mongodb")
                .name("MongoDB")
                .description("NoSQL document-oriented database")
                .type(ComponentType.DATABASE)
                .dependencies(List.of(
                        "pymongo==4.6.1"
                ))
                .templatePaths(List.of(
                        "database/mongodb/db_connection.py.ftl"
                ))
                .documentationPaths(List.of(
                        "database/mongodb/DB_USAGE_INSTRUCTIONS.md.ftl"
                ))
                .conflicts(Set.of("sqlite"))
                .build();

        // SQLite
        Component sqlite = Component.builder()
                .id("sqlite")
                .name("SQLite")
                .description("Lightweight embedded SQL database")
                .type(ComponentType.DATABASE)
                .dependencies(List.of(
                        "SQLAlchemy==2.0.25"
                ))
                .templatePaths(List.of(
                        "database/sqlite/db_connection.py.ftl"
                ))
                .documentationPaths(List.of(
                        "database/sqlite/DB_USAGE_INSTRUCTIONS.md.ftl"
                ))
                .conflicts(Set.of("postgresql", "mysql", "mongodb", "redis"))
                .build();

        // Redis
        Component redis = Component.builder()
                .id("redis")
                .name("Redis")
                .description("In-memory data structure store for caching")
                .type(ComponentType.DATABASE)
                .dependencies(List.of(
                        "redis==5.0.1"
                ))
                .templatePaths(List.of(
                        "database/redis/db_connection.py.ftl"
                ))
                .documentationPaths(List.of(
                        "database/redis/DB_USAGE_INSTRUCTIONS.md.ftl"
                ))
                .conflicts(Set.of())
                .build();

        // ========== TESTING ==========

        // pytest
        Component pytest = Component.builder()
                .id("pytest")
                .name("pytest")
                .description("Python testing framework")
                .type(ComponentType.TESTING)
                .dependencies(List.of(
                        "pytest==7.4.4",
                        "pytest-cov==4.1.0"
                ))
                .templatePaths(List.of())
                .documentationPaths(List.of())
                .build();

        // unittest (built-in, no dependencies)
        Component unittest = Component.builder()
                .id("unittest")
                .name("unittest")
                .description("Python's built-in testing framework")
                .type(ComponentType.TESTING)
                .dependencies(List.of())
                .templatePaths(List.of(
                        "testing/unittest/test_example.py.ftl"
                ))
                .documentationPaths(List.of())
                .build();

        // ========== SECURITY ==========

        // JWT Authentication
        Component jwt = Component.builder()
                .id("jwt")
                .name("JWT Authentication")
                .description("JSON Web Token authentication for APIs")
                .type(ComponentType.SECURITY)
                .dependencies(List.of(
                        "PyJWT==2.8.0",
                        "cryptography==41.0.7"
                ))
                .templatePaths(List.of(
                        "security/jwt/jwt_handler.py.ftl"
                ))
                .documentationPaths(List.of(
                        "security/jwt/SECURITY_README.md.ftl"
                ))
                .requires(Set.of("flask", "fastapi", "django"))
                .build();

        // OAuth2
        Component oauth2 = Component.builder()
                .id("oauth2")
                .name("OAuth2")
                .description("OAuth2 authentication and authorization")
                .type(ComponentType.SECURITY)
                .dependencies(List.of(
                        "authlib==1.3.0"
                ))
                .templatePaths(List.of(
                        "security/oauth2/oauth_handler.py.ftl"
                ))
                .documentationPaths(List.of(
                        "security/oauth2/SECURITY_README.md.ftl"
                ))
                .requires(Set.of("flask", "fastapi", "django"))
                .build();

        // Basic Auth
        Component basicAuth = Component.builder()
                .id("basicauth")
                .name("Basic Auth")
                .description("HTTP Basic Authentication")
                .type(ComponentType.SECURITY)
                .dependencies(List.of())
                .templatePaths(List.of(
                        "security/basicauth/auth_handler.py.ftl"
                ))
                .documentationPaths(List.of())
                .requires(Set.of("flask", "fastapi", "django"))
                .build();

        // API Key
        Component apiKey = Component.builder()
                .id("apikey")
                .name("API Key")
                .description("API Key-based authentication")
                .type(ComponentType.SECURITY)
                .dependencies(List.of())
                .templatePaths(List.of(
                        "security/apikey/auth_handler.py.ftl"
                ))
                .documentationPaths(List.of())
                .requires(Set.of("flask", "fastapi", "django"))
                .build();

        // ========== GUI FRAMEWORKS ==========

        // Tkinter (built-in, no dependencies)
        Component tkinter = Component.builder()
                .id("tkinter")
                .name("Tkinter")
                .description("Python's built-in GUI framework")
                .type(ComponentType.GUI)
                .dependencies(List.of())
                .templatePaths(List.of(
                        "gui/tkinter/gui_app.py.ftl"
                ))
                .documentationPaths(List.of())
                .conflicts(Set.of("flask", "fastapi", "django", "pyqt5", "streamlit"))
                .build();

        // PyQt5
        Component pyqt5 = Component.builder()
                .id("pyqt5")
                .name("PyQt5")
                .description("Cross-platform GUI framework based on Qt")
                .type(ComponentType.GUI)
                .dependencies(List.of(
                        "PyQt5==5.15.10"
                ))
                .templatePaths(List.of(
                        "gui/pyqt5/gui_app.py.ftl"
                ))
                .documentationPaths(List.of())
                .conflicts(Set.of("flask", "fastapi", "django", "tkinter", "streamlit"))
                .build();

        // Streamlit
        Component streamlit = Component.builder()
                .id("streamlit")
                .name("Streamlit")
                .description("Framework for creating data apps and dashboards")
                .type(ComponentType.GUI)
                .dependencies(List.of(
                        "streamlit==1.31.0"
                ))
                .templatePaths(List.of(
                        "gui/streamlit/streamlit_app.py.ftl"
                ))
                .documentationPaths(List.of())
                .conflicts(Set.of("tkinter", "pyqt5"))
                .build();

        // ========== DOCKER ==========

        // Docker
        Component docker = Component.builder()
                .id("docker")
                .name("Docker")
                .description("Docker containerization support")
                .type(ComponentType.DOCKER)
                .dependencies(List.of())
                .templatePaths(List.of(
                        "docker/Dockerfile.ftl",
                        "docker/docker-compose.yml.ftl",
                        "docker/.dockerignore.ftl"
                ))
                .documentationPaths(List.of())
                .build();

        // Register all hardcoded components
        hardcodedComponents.put(flask.getId(), flask);
        hardcodedComponents.put(fastapi.getId(), fastapi);
        hardcodedComponents.put(django.getId(), django);
        hardcodedComponents.put(postgresql.getId(), postgresql);
        hardcodedComponents.put(mysql.getId(), mysql);
        hardcodedComponents.put(mongodb.getId(), mongodb);
        hardcodedComponents.put(sqlite.getId(), sqlite);
        hardcodedComponents.put(redis.getId(), redis);
        hardcodedComponents.put(pytest.getId(), pytest);
        hardcodedComponents.put(unittest.getId(), unittest);
        hardcodedComponents.put(jwt.getId(), jwt);
        hardcodedComponents.put(oauth2.getId(), oauth2);
        hardcodedComponents.put(basicAuth.getId(), basicAuth);
        hardcodedComponents.put(apiKey.getId(), apiKey);
        hardcodedComponents.put(tkinter.getId(), tkinter);
        hardcodedComponents.put(pyqt5.getId(), pyqt5);
        hardcodedComponents.put(streamlit.getId(), streamlit);
        hardcodedComponents.put(docker.getId(), docker);

        log.info("Registered {} hardcoded components", hardcodedComponents.size());
        return hardcodedComponents;
    }

    /**
     * Get a component by ID
     */
    public Optional<Component> getComponent(String id) {
        return Optional.ofNullable(components.get(id));
    }

    /**
     * Get all components
     */
    public Collection<Component> getAllComponents() {
        return components.values();
    }

    /**
     * Get components by type
     */
    public List<Component> getComponentsByType(ComponentType type) {
        return components.values().stream()
                .filter(c -> c.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Check if a component exists
     */
    public boolean hasComponent(String id) {
        return components.containsKey(id);
    }

    /**
     * Get project metadata for the frontend
     */
    public ProjectMetadata getMetadata() {
        List<ComponentOption> componentOptions = components.values().stream()
                .map(this::toComponentOption)
                .collect(Collectors.toList());

        return ProjectMetadata.builder()
                .version("1.0.0")
                .defaultPythonVersion("3.11")
                .supportedPythonVersions(List.of("3.9", "3.10", "3.11", "3.12"))
                .components(componentOptions)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    /**
     * Convert Component to ComponentOption DTO
     */
    private ComponentOption toComponentOption(Component component) {
        return ComponentOption.builder()
                .id(component.getId())
                .name(component.getName())
                .description(component.getDescription())
                .type(component.getType())
                .dependencies(component.getDependencies())
                .conflicts(component.getConflicts())
                .requires(component.getRequires())
                .build();
    }
}
