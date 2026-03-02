package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.exception.IncompatibleComponentsException;
import com.stephen_oosthuizen.genesis.metadata.ComponentRegistry;
import com.stephen_oosthuizen.genesis.model.Component;
import com.stephen_oosthuizen.genesis.model.ComponentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for resolving component dependencies and detecting conflicts.
 * Validates component compatibility and provides helpful error messages.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DependencyResolver {
    private final ComponentRegistry componentRegistry;

    /**
     * Validate that selected components are compatible
     *
     * @param components Set of components to validate
     * @throws IncompatibleComponentsException if components conflict
     */
    public void validateCompatibility(Set<Component> components) {
        log.debug("Validating component compatibility for {} components", components.size());

        // Check for explicit conflicts
        checkExplicitConflicts(components);

        // Check for requirement violations
        checkRequirements(components);

        // Check for incompatible component type combinations
        checkTypeCombinations(components);

        log.debug("Component validation passed");
    }

    /**
     * Check for explicit conflicts defined in component metadata
     */
    private void checkExplicitConflicts(Set<Component> components) {
        for (Component component : components) {
            for (String conflictId : component.getConflicts()) {
                boolean hasConflict = components.stream()
                        .anyMatch(c -> c.getId().equals(conflictId));

                if (hasConflict) {
                    String message = String.format(
                            "Components '%s' and '%s' cannot be used together",
                            component.getName(),
                            conflictId
                    );
                    log.warn("Conflict detected: {}", message);
                    throw new IncompatibleComponentsException(
                            message,
                            Set.of(component.getId(), conflictId)
                    );
                }
            }
        }
    }

    /**
     * Check that required components are present
     */
    private void checkRequirements(Set<Component> components) {
        for (Component component : components) {
            if (!component.getRequires().isEmpty()) {
                boolean hasRequiredComponent = component.getRequires().stream()
                        .anyMatch(requiredId -> components.stream()
                                .anyMatch(c -> c.getId().equals(requiredId)));

                if (!hasRequiredComponent) {
                    String requiredNames = component.getRequires().stream()
                            .map(id -> componentRegistry.getComponent(id)
                                    .map(Component::getName)
                                    .orElse(id))
                            .collect(Collectors.joining(", "));

                    String message = String.format(
                            "'%s' requires one of: %s",
                            component.getName(),
                            requiredNames
                    );
                    log.warn("Requirement violation: {}", message);
                    throw new IncompatibleComponentsException(
                            message,
                            Set.of(component.getId())
                    );
                }
            }
        }
    }

    /**
     * Check for incompatible component type combinations
     */
    private void checkTypeCombinations(Set<Component> components) {
        boolean hasWebFramework = components.stream()
                .anyMatch(c -> c.getType() == ComponentType.WEB_FRAMEWORK);

        boolean hasGui = components.stream()
                .anyMatch(c -> c.getType() == ComponentType.GUI);

        // GUI frameworks (except Streamlit) conflict with web frameworks
        if (hasWebFramework && hasGui) {
            Component guiComponent = components.stream()
                    .filter(c -> c.getType() == ComponentType.GUI)
                    .findFirst()
                    .orElse(null);

            // Streamlit is compatible with web frameworks
            if (guiComponent != null && !guiComponent.getId().equals("streamlit")) {
                String message = String.format(
                        "Desktop GUI frameworks (%s) cannot be combined with web frameworks. Use Streamlit for web-based dashboards instead.",
                        guiComponent.getName()
                );
                log.warn("Type conflict detected: {}", message);
                throw new IncompatibleComponentsException(
                        message,
                        Set.of(guiComponent.getId())
                );
            }
        }

        // Check for multiple web frameworks
        long webFrameworkCount = components.stream()
                .filter(c -> c.getType() == ComponentType.WEB_FRAMEWORK)
                .count();

        if (webFrameworkCount > 1) {
            String frameworks = components.stream()
                    .filter(c -> c.getType() == ComponentType.WEB_FRAMEWORK)
                    .map(Component::getName)
                    .collect(Collectors.joining(", "));

            String message = String.format(
                    "Cannot use multiple web frameworks: %s. Please select only one.",
                    frameworks
            );
            log.warn("Multiple web frameworks: {}", message);
            throw new IncompatibleComponentsException(
                    message,
                    components.stream()
                            .filter(c -> c.getType() == ComponentType.WEB_FRAMEWORK)
                            .map(Component::getId)
                            .collect(Collectors.toSet())
            );
        }
    }

    /**
     * Get validation warnings (non-fatal issues)
     */
    public List<String> getWarnings(Set<Component> components) {
        List<String> warnings = new ArrayList<>();

        // Warn if no testing framework
        boolean hasTesting = components.stream()
                .anyMatch(c -> c.getType() == ComponentType.TESTING);
        if (!hasTesting) {
            warnings.add("No testing framework selected. Consider adding pytest or unittest.");
        }

        // Warn if security but no web framework
        boolean hasSecurity = components.stream()
                .anyMatch(c -> c.getType() == ComponentType.SECURITY);
        boolean hasWebFramework = components.stream()
                .anyMatch(c -> c.getType() == ComponentType.WEB_FRAMEWORK);

        if (hasSecurity && !hasWebFramework) {
            warnings.add("Security components are designed for web applications. Consider adding a web framework.");
        }

        return warnings;
    }
}
