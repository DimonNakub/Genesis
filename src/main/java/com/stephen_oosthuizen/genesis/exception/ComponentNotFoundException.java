package com.stephen_oosthuizen.genesis.exception;

/**
 * Exception thrown when a requested component is not found in the registry.
 */
public class ComponentNotFoundException extends RuntimeException {
    public ComponentNotFoundException(String message) {
        super(message);
    }
}
