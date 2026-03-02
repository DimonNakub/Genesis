package com.stephen_oosthuizen.genesis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO containing all available components and metadata for the frontend.
 * Returned by GET /api/v1/metadata endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMetadata {
    /**
     * Application version
     */
    @Builder.Default
    private String version = "1.0.0";

    /**
     * Default Python version
     */
    @Builder.Default
    private String defaultPythonVersion = "3.11";

    /**
     * Supported Python versions
     */
    @Builder.Default
    private List<String> supportedPythonVersions = List.of("3.9", "3.10", "3.11", "3.12");

    /**
     * List of all available components
     */
    @Builder.Default
    private List<ComponentOption> components = new ArrayList<>();

    /**
     * Metadata timestamp
     */
    private String timestamp;
}
