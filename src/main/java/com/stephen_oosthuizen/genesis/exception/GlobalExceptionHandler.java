package com.stephen_oosthuizen.genesis.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 * Converts exceptions to appropriate HTTP error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid request parameters")
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle component not found errors
     */
    @ExceptionHandler(ComponentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleComponentNotFound(
            ComponentNotFoundException ex) {
        log.warn("Component not found: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Component Not Found")
                .message(ex.getMessage() + ". Please check the component ID and try again.")
                .details(Map.of(
                        "suggestion", "Use GET /api/v1/metadata to see available components"
                ))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle incompatible components errors
     */
    @ExceptionHandler(IncompatibleComponentsException.class)
    public ResponseEntity<ErrorResponse> handleIncompatibleComponents(
            IncompatibleComponentsException ex) {
        log.warn("❌ Incompatible components detected: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        details.put("conflicts", ex.getConflicts());
        details.put("suggestion", "Please review your component selections. Some components cannot be used together.");

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Incompatible Components")
                .message(ex.getMessage())
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle template not found errors
     */
    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTemplateNotFound(
            TemplateNotFoundException ex) {
        log.error("Template not found: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Template Error")
                .message("Failed to generate project. Please contact support.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle generation errors
     */
    @ExceptionHandler(GenerationException.class)
    public ResponseEntity<ErrorResponse> handleGenerationException(
            GenerationException ex) {
        log.error("Generation failed: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Generation Failed")
                .message("Failed to generate project: " + ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Error response DTO
     */
    @lombok.Builder
    @lombok.Data
    private static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, ?> details;
    }
}
