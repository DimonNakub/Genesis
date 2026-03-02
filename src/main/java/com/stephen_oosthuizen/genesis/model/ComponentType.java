package com.stephen_oosthuizen.genesis.model;

/**
 * Enum representing the different types of components that can be included in a Python project.
 * Each type represents a category of functionality (e.g., databases, web frameworks, security).
 */
public enum ComponentType {
    /**
     * Database connection components (PostgreSQL, MySQL, MongoDB, SQLite, Redis)
     */
    DATABASE("Database"),

    /**
     * Web framework components (Flask, FastAPI, Django)
     */
    WEB_FRAMEWORK("Web Framework"),

    /**
     * Security and authentication components (JWT, OAuth2, Basic Auth, API Key)
     */
    SECURITY("Security"),

    /**
     * GUI framework components (Tkinter, PyQt5, Streamlit)
     */
    GUI("GUI Framework"),

    /**
     * Testing framework components (pytest, unittest)
     */
    TESTING("Testing"),

    /**
     * Docker and containerization support
     */
    DOCKER("Docker"),

    /**
     * Utility and configuration components
     */
    UTILITY("Utility");

    private final String displayName;

    ComponentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
