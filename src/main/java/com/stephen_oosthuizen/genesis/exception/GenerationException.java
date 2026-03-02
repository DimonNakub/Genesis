package com.stephen_oosthuizen.genesis.exception;

/**
 * Exception thrown when project generation fails.
 */
public class GenerationException extends RuntimeException {
    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
