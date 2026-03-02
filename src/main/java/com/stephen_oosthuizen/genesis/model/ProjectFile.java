package com.stephen_oosthuizen.genesis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single file in the generated Python project.
 * Contains the file path and content.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFile {
    /**
     * Relative path within the project (e.g., "app.py", "config/database.py", "README.md")
     */
    private String path;

    /**
     * Content of the file
     */
    private String content;

    /**
     * Whether the file should be executable (for scripts)
     */
    @Builder.Default
    private boolean executable = false;

    /**
     * MIME type of the file (optional, for special handling)
     */
    private String mimeType;

    /**
     * Create a ProjectFile from path and content
     */
    public static ProjectFile of(String path, String content) {
        return ProjectFile.builder()
                .path(path)
                .content(content)
                .build();
    }

    /**
     * Create an executable script file
     */
    public static ProjectFile executable(String path, String content) {
        return ProjectFile.builder()
                .path(path)
                .content(content)
                .executable(true)
                .build();
    }
}
