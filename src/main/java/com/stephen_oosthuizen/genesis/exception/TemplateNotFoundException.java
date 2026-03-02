package com.stephen_oosthuizen.genesis.exception;

/**
 * Exception thrown when a template file cannot be found.
 */
public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(String message) {
        super(message);
    }

    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
