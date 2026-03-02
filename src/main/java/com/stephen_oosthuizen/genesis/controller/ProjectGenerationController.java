package com.stephen_oosthuizen.genesis.controller;

import com.stephen_oosthuizen.genesis.dto.ProjectMetadata;
import com.stephen_oosthuizen.genesis.dto.ProjectRequest;
import com.stephen_oosthuizen.genesis.metadata.ComponentRegistry;
import com.stephen_oosthuizen.genesis.service.ProjectGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for Python project generation.
 * Provides endpoints for metadata retrieval and project generation.
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow all origins for development
public class ProjectGenerationController {
    private final ProjectGenerationService generationService;
    private final ComponentRegistry componentRegistry;

    /**
     * Get metadata about available components
     *
     * @return ProjectMetadata with all available components
     */
    @GetMapping("/metadata")
    public ResponseEntity<ProjectMetadata> getMetadata() {
        log.info("GET /api/v1/metadata - Fetching component metadata");

        ProjectMetadata metadata = componentRegistry.getMetadata();

        log.info("Returning metadata with {} components", metadata.getComponents().size());
        return ResponseEntity.ok(metadata);
    }

    /**
     * Generate a Python project based on user selections
     *
     * @param request ProjectRequest with user selections
     * @return ZIP file as byte array
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateProject(@Valid @RequestBody ProjectRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("POST /api/v1/generate - Generating project: {}", request.getProjectName());
        log.info("  Components: {}", request.getComponents());
        log.info("  Python version: {}", request.getPythonVersion());

        try {
            // Generate project
            byte[] zipFile = generationService.generateProject(request);

            // Build response with appropriate headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", request.getProjectName() + ".zip");
            headers.setContentLength(zipFile.length);

            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ Project generated successfully: {} ({} bytes, {}ms)",
                    request.getProjectName(), zipFile.length, duration);

            return new ResponseEntity<>(zipFile, headers, HttpStatus.OK);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Project generation failed: {} ({}ms) - {}",
                    request.getProjectName(), duration, e.getMessage());
            throw e;
        }
    }

    /**
     * Health check endpoint
     *
     * @return Detailed health status with system info
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("application", "Genesis Python Initializr");
        health.put("version", "1.0.0");
        health.put("componentsAvailable", componentRegistry.getAllComponents().size());
        health.put("timestamp", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME));

        return ResponseEntity.ok(health);
    }
}
