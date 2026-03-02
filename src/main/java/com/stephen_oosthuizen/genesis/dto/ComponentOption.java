package com.stephen_oosthuizen.genesis.dto;

import com.stephen_oosthuizen.genesis.model.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DTO representing a component option available for selection in the frontend.
 * Contains metadata for UI display and validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentOption {
    /**
     * Unique component identifier
     */
    private String id;

    /**
     * Display name
     */
    private String name;

    /**
     * Detailed description
     */
    private String description;

    /**
     * Component type/category
     */
    private ComponentType type;

    /**
     * List of dependency package names (for UI display)
     */
    @Builder.Default
    private List<String> dependencies = new ArrayList<>();

    /**
     * Component IDs that conflict with this one
     */
    @Builder.Default
    private Set<String> conflicts = new HashSet<>();

    /**
     * Component IDs that this component requires
     */
    @Builder.Default
    private Set<String> requires = new HashSet<>();
}
