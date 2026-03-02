package com.stephen_oosthuizen.genesis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for incoming project generation requests.
 * Represents the user's selections and project metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    /**
     * Project name in snake_case format
     * Must be alphanumeric with underscores only
     */
    @NotBlank(message = "Project name is required")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "Project name must be lowercase with underscores only")
    @Size(min = 2, max = 50, message = "Project name must be between 2 and 50 characters")
    private String projectName;

    /**
     * Brief project description
     */
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    /**
     * Python version (e.g., "3.11", "3.10", "3.9")
     */
    @Pattern(regexp = "^3\\.(9|10|11|12)$", message = "Python version must be 3.9, 3.10, 3.11, or 3.12")
    @Builder.Default
    private String pythonVersion = "3.11";

    /**
     * Author/developer name
     */
    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String authorName;

    /**
     * Author email address
     */
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String authorEmail;

    /**
     * List of component IDs to include (e.g., ["flask", "postgresql", "jwt"])
     */
    @Builder.Default
    private List<String> components = new ArrayList<>();
}
