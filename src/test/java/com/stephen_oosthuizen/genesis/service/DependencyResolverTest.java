package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.exception.IncompatibleComponentsException;
import com.stephen_oosthuizen.genesis.metadata.ComponentRegistry;
import com.stephen_oosthuizen.genesis.model.Component;
import com.stephen_oosthuizen.genesis.model.ComponentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for DependencyResolver
 * Tests conflict detection, requirement validation, and warnings
 */
@SpringBootTest
class DependencyResolverTest {

    @Autowired
    private DependencyResolver dependencyResolver;

    @Autowired
    private ComponentRegistry componentRegistry;

    @Test
    void shouldDetectWebFrameworkConflicts() {
        // Given - Flask and Django (conflicting)
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("flask").orElseThrow());
        components.add(componentRegistry.getComponent("django").orElseThrow());

        // When & Then
        assertThatThrownBy(() -> dependencyResolver.validateCompatibility(components))
                .isInstanceOf(IncompatibleComponentsException.class)
                .hasMessageContaining("flask")
                .hasMessageContaining("django");
    }

    @Test
    void shouldDetectMultipleWebFrameworks() {
        // Given - FastAPI, Flask, Django (multiple web frameworks)
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("flask").orElseThrow());
        components.add(componentRegistry.getComponent("fastapi").orElseThrow());
        components.add(componentRegistry.getComponent("django").orElseThrow());

        // When & Then
        assertThatThrownBy(() -> dependencyResolver.validateCompatibility(components))
                .isInstanceOf(IncompatibleComponentsException.class)
                .hasMessageContaining("multiple web frameworks");
    }

    @Test
    void shouldDetectGuiWebFrameworkConflict() {
        // Given - Tkinter (desktop GUI) + Flask (web framework)
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("tkinter").orElseThrow());
        components.add(componentRegistry.getComponent("flask").orElseThrow());

        // When & Then
        assertThatThrownBy(() -> dependencyResolver.validateCompatibility(components))
                .isInstanceOf(IncompatibleComponentsException.class)
                .hasMessageContaining("Desktop GUI frameworks")
                .hasMessageContaining("cannot be combined with web frameworks");
    }

    @Test
    void shouldAllowStreamlitWithWebFramework() {
        // Given - Streamlit (web-based GUI) + FastAPI
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("streamlit").orElseThrow());
        components.add(componentRegistry.getComponent("fastapi").orElseThrow());

        // When & Then - should NOT throw exception
        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldDetectSecurityWithoutWebFramework() {
        // Given - JWT without any web framework
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("jwt").orElseThrow());

        // When & Then
        assertThatThrownBy(() -> dependencyResolver.validateCompatibility(components))
                .isInstanceOf(IncompatibleComponentsException.class)
                .hasMessageContaining("requires");
    }

    @Test
    void shouldAllowSecurityWithWebFramework() {
        // Given - JWT with FastAPI
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("jwt").orElseThrow());
        components.add(componentRegistry.getComponent("fastapi").orElseThrow());

        // When & Then - should NOT throw exception
        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAllowMultipleDatabases() {
        // Given - PostgreSQL + Redis (compatible)
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("postgresql").orElseThrow());
        components.add(componentRegistry.getComponent("redis").orElseThrow());

        // When & Then - should NOT throw exception
        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldDetectSqliteWithServerDatabase() {
        // Given - SQLite + PostgreSQL (conflicting)
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("sqlite").orElseThrow());
        components.add(componentRegistry.getComponent("postgresql").orElseThrow());

        // When & Then
        assertThatThrownBy(() -> dependencyResolver.validateCompatibility(components))
                .isInstanceOf(IncompatibleComponentsException.class)
                .hasMessageContaining("cannot be used together");
    }

    @Test
    void shouldWarnWhenNoTestingFramework() {
        // Given - components without testing
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("flask").orElseThrow());

        // When
        List<String> warnings = dependencyResolver.getWarnings(components);

        // Then
        assertThat(warnings).isNotEmpty();
        assertThat(warnings.get(0)).contains("No testing framework");
    }

    @Test
    void shouldNotWarnWhenTestingFrameworkPresent() {
        // Given - components with pytest
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("flask").orElseThrow());
        components.add(componentRegistry.getComponent("pytest").orElseThrow());

        // When
        List<String> warnings = dependencyResolver.getWarnings(components);

        // Then - no testing warning
        assertThat(warnings.stream().noneMatch(w -> w.contains("testing"))).isTrue();
    }

    @Test
    void shouldValidateEmptyComponentSet() {
        // Given - empty set
        Set<Component> components = new HashSet<>();

        // When & Then - should not throw
        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldValidateComplexValidConfiguration() {
        // Given - FastAPI + PostgreSQL + Redis + JWT + Docker + pytest
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("fastapi").orElseThrow());
        components.add(componentRegistry.getComponent("postgresql").orElseThrow());
        components.add(componentRegistry.getComponent("redis").orElseThrow());
        components.add(componentRegistry.getComponent("jwt").orElseThrow());
        components.add(componentRegistry.getComponent("docker").orElseThrow());
        components.add(componentRegistry.getComponent("pytest").orElseThrow());

        // When & Then - should NOT throw exception
        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldAllowAllSecurityOptionsWithWebFramework() {
        // Given - All security options with Flask
        Set<Component> components = new HashSet<>();
        components.add(componentRegistry.getComponent("flask").orElseThrow());
        components.add(componentRegistry.getComponent("jwt").orElseThrow());

        // When & Then
        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();

        // Test with OAuth2
        components.clear();
        components.add(componentRegistry.getComponent("flask").orElseThrow());
        components.add(componentRegistry.getComponent("oauth2").orElseThrow());

        assertThatCode(() -> dependencyResolver.validateCompatibility(components))
                .doesNotThrowAnyException();
    }
}
