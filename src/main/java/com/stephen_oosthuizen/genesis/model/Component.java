package com.stephen_oosthuizen.genesis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a component that can be included in a generated Python project.
 * Each component has metadata including dependencies, templates, and potential conflicts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Component {
    /**
     * Unique identifier for the component (e.g., "postgresql", "flask", "jwt")
     */
    private String id;

    /**
     * Display name for the component (e.g., "PostgreSQL", "Flask", "JWT Authentication")
     */
    private String name;

    /**
     * Detailed description of the component
     */
    private String description;

    /**
     * The type/category of this component
     */
    private ComponentType type;

    /**
     * List of Python package dependencies with versions (e.g., "Flask==3.0.0")
     */
    @Builder.Default
    private List<String> dependencies = new ArrayList<>();

    /**
     * List of template file paths to render for this component (e.g., "web/flask/app.py.ftl")
     */
    @Builder.Default
    private List<String> templatePaths = new ArrayList<>();

    /**
     * List of documentation template paths (e.g., "database/postgresql/DB_USAGE_INSTRUCTIONS.md.ftl")
     */
    @Builder.Default
    private List<String> documentationPaths = new ArrayList<>();

    /**
     * Set of component IDs that conflict with this component (e.g., Flask conflicts with Django)
     */
    @Builder.Default
    private Set<String> conflicts = new HashSet<>();

    /**
     * Set of component IDs that this component requires (e.g., JWT requires a web framework)
     */
    @Builder.Default
    private Set<String> requires = new HashSet<>();
}
