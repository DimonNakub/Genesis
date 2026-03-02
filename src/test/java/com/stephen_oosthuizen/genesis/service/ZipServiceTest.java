package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.exception.GenerationException;
import com.stephen_oosthuizen.genesis.model.ProjectFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ZipService
 * Tests ZIP creation, file structure, and permissions
 */
class ZipServiceTest {

    private ZipService zipService;

    @BeforeEach
    void setUp() {
        zipService = new ZipService();
    }

    @Test
    void shouldCreateValidZipFile() throws IOException {
        // Given
        List<ProjectFile> files = List.of(
                ProjectFile.of("README.md", "# Test Project"),
                ProjectFile.of("main.py", "print('Hello World')")
        );

        // When
        byte[] zipBytes = zipService.createZip(files, "test_project");

        // Then
        assertThat(zipBytes).isNotNull();
        assertThat(zipBytes.length).isGreaterThan(0);

        // Verify ZIP can be extracted
        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertThat(extractedFiles).hasSize(2);
        assertThat(extractedFiles).containsKey("test_project/README.md");
        assertThat(extractedFiles).containsKey("test_project/main.py");
        assertThat(extractedFiles.get("test_project/README.md")).isEqualTo("# Test Project");
        assertThat(extractedFiles.get("test_project/main.py")).isEqualTo("print('Hello World')");
    }

    @Test
    void shouldCreateCorrectDirectoryStructure() throws IOException {
        // Given
        List<ProjectFile> files = List.of(
                ProjectFile.of("main.py", "content"),
                ProjectFile.of("config/config.py", "content"),
                ProjectFile.of("config/db_connection.py", "content")
        );

        // When
        byte[] zipBytes = zipService.createZip(files, "my_project");

        // Then
        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertThat(extractedFiles).containsKey("my_project/main.py");
        assertThat(extractedFiles).containsKey("my_project/config/config.py");
        assertThat(extractedFiles).containsKey("my_project/config/db_connection.py");
    }

    @Test
    void shouldSetExecutablePermissionsForScripts() throws IOException {
        // Given
        List<ProjectFile> files = List.of(
                ProjectFile.of("README.md", "readme content"),
                ProjectFile.executable("setup_venv.sh", "#!/bin/bash\necho 'setup'"),
                ProjectFile.executable("run_dev.sh", "#!/bin/bash\necho 'run'")
        );

        // When
        byte[] zipBytes = zipService.createZip(files, "test_project");

        // Then - verify executable permissions
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
             ZipArchiveInputStream zis = new ZipArchiveInputStream(bais)) {

            ZipArchiveEntry entry;
            Map<String, Integer> permissions = new HashMap<>();

            while ((entry = zis.getNextZipEntry()) != null) {
                permissions.put(entry.getName(), entry.getUnixMode());
            }

            // Regular file should have 0644 (rw-r--r--)
            assertThat(permissions.get("test_project/README.md")).isEqualTo(0644);

            // Executable scripts should have 0755 (rwxr-xr-x)
            assertThat(permissions.get("test_project/setup_venv.sh")).isEqualTo(0755);
            assertThat(permissions.get("test_project/run_dev.sh")).isEqualTo(0755);
        }
    }

    @Test
    void shouldHandleEmptyFileList() {
        // Given
        List<ProjectFile> emptyFiles = new ArrayList<>();

        // When
        byte[] zipBytes = zipService.createZip(emptyFiles, "empty_project");

        // Then
        assertThat(zipBytes).isNotNull();
        assertThat(zipBytes.length).isGreaterThan(0);
    }

    @Test
    void shouldHandleLargeFiles() {
        // Given - large file content
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("# This is line ").append(i).append("\n");
        }

        List<ProjectFile> files = List.of(
                ProjectFile.of("large_file.py", largeContent.toString())
        );

        // When
        byte[] zipBytes = zipService.createZip(files, "large_project");

        // Then
        assertThat(zipBytes).isNotNull();
        assertThat(zipBytes.length).isGreaterThan(0);
    }

    @Test
    void shouldHandleSpecialCharactersInFilenames() throws IOException {
        // Given
        List<ProjectFile> files = List.of(
                ProjectFile.of(".env.template", "ENV=value"),
                ProjectFile.of(".gitignore", "*.pyc"),
                ProjectFile.of("file-with-dashes.py", "content")
        );

        // When
        byte[] zipBytes = zipService.createZip(files, "special_chars");

        // Then
        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertThat(extractedFiles).containsKey("special_chars/.env.template");
        assertThat(extractedFiles).containsKey("special_chars/.gitignore");
        assertThat(extractedFiles).containsKey("special_chars/file-with-dashes.py");
    }

    @Test
    void shouldPreserveFileContent() throws IOException {
        // Given - file with special content
        String pythonCode = "def hello():\n    print('Hello, World!')\n\nif __name__ == '__main__':\n    hello()";
        List<ProjectFile> files = List.of(
                ProjectFile.of("script.py", pythonCode)
        );

        // When
        byte[] zipBytes = zipService.createZip(files, "test");

        // Then
        Map<String, String> extractedFiles = extractZipContents(zipBytes);
        assertThat(extractedFiles.get("test/script.py")).isEqualTo(pythonCode);
    }

    /**
     * Helper method to extract and read ZIP contents
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
