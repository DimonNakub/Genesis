package com.stephen_oosthuizen.genesis.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Context object passed to FreeMarker templates for rendering.
 * Contains all variables and helper methods available in templates.
 */
@Data
public class TemplateContext {
    // Project Metadata
    private String projectName;
    private String description;
    private String pythonVersion;
    private String authorName;
    private String authorEmail;

    // Components
    private Set<Component> components;

    // Custom Variables (for template-specific data)
    private Map<String, Object> customVariables = new HashMap<>();

    /**
     * Constructor that builds context from ProjectConfiguration
     */
    public TemplateContext(ProjectConfiguration config) {
        this.projectName = config.getProjectName();
        this.description = config.getDescription();
        this.pythonVersion = config.getPythonVersion();
        this.authorName = config.getAuthorName();
        this.authorEmail = config.getAuthorEmail();
        this.components = config.getComponents();
    }

    // Helper methods available in templates

    public boolean hasDatabase() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.DATABASE);
    }

    public boolean hasWebFramework() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.WEB_FRAMEWORK);
    }

    public boolean hasSecurity() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.SECURITY);
    }

    public boolean hasGui() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.GUI);
    }

    public boolean hasTesting() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.TESTING);
    }

    public boolean hasDocker() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.DOCKER);
    }

    public boolean hasComponent(String componentId) {
        return components.stream()
                .anyMatch(c -> c.getId().equals(componentId));
    }

    public String getMainFramework() {
        return components.stream()
                .filter(c -> c.getType() == ComponentType.WEB_FRAMEWORK)
                .findFirst()
                .map(Component::getId)
                .orElse("none");
    }

    public String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getCurrentYear() {
        return String.valueOf(LocalDateTime.now().getYear());
    }

    /**
     * Get all dependencies for included components
     */
    public Set<String> getAllDependencies() {
        return components.stream()
                .flatMap(c -> c.getDependencies().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Add custom variable for template-specific data
     */
    public void addCustomVariable(String key, Object value) {
        this.customVariables.put(key, value);
    }
}
