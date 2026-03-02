package com.stephen_oosthuizen.genesis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Complete configuration for a Python project to be generated.
 * Contains all project metadata and selected components.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectConfiguration {
    /**
     * Project name in snake_case format (e.g., "my_project")
     */
    private String projectName;

    /**
     * Brief description of the project
     */
    private String description;

    /**
     * Python version to use (e.g., "3.11")
     */
    @Builder.Default
    private String pythonVersion = "3.11";

    /**
     * Author/developer name
     */
    private String authorName;

    /**
     * Author email address
     */
    private String authorEmail;

    /**
     * Set of all selected components (including resolved dependencies)
     */
    @Builder.Default
    private Set<Component> components = new HashSet<>();

    /**
     * Check if the project includes database components
     */
    public boolean hasDatabase() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.DATABASE);
    }

    /**
     * Check if the project includes a web framework
     */
    public boolean hasWebFramework() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.WEB_FRAMEWORK);
    }

    /**
     * Check if the project includes security components
     */
    public boolean hasSecurity() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.SECURITY);
    }

    /**
     * Check if the project includes GUI components
     */
    public boolean hasGui() {
        return components.stream()
                .anyMatch(c -> c.getType() == ComponentType.GUI);
    }

    /**
     * Check if a specific component is included
     */
    public boolean hasComponent(String componentId) {
        return components.stream()
                .anyMatch(c -> c.getId().equals(componentId));
    }

    /**
     * Get the main web framework component (if any)
     */
    public Component getMainWebFramework() {
        return components.stream()
                .filter(c -> c.getType() == ComponentType.WEB_FRAMEWORK)
                .findFirst()
                .orElse(null);
    }
}
