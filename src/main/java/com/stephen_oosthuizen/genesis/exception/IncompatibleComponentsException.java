package com.stephen_oosthuizen.genesis.exception;

import lombok.Getter;

import java.util.Set;

/**
 * Exception thrown when incompatible components are selected together.
 */
@Getter
public class IncompatibleComponentsException extends RuntimeException {
    private final Set<String> conflicts;

    public IncompatibleComponentsException(String message, Set<String> conflicts) {
        super(message);
        this.conflicts = conflicts;
    }
}
