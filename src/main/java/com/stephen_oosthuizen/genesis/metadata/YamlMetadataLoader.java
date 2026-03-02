package com.stephen_oosthuizen.genesis.metadata;

import com.stephen_oosthuizen.genesis.model.Component;
import com.stephen_oosthuizen.genesis.model.ComponentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * Loads component metadata from YAML configuration file.
 * Parses components.yaml and creates Component objects.
 */
@Service
@Slf4j
public class YamlMetadataLoader {
    private static final String COMPONENTS_FILE = "templates/python/metadata/components.yaml";

    /**
     * Load all components from YAML file
     *
     * @return Map of component ID to Component object
     */
    public Map<String, Component> loadComponents() {
        log.info("Loading components from YAML: {}", COMPONENTS_FILE);

        try {
            ClassPathResource resource = new ClassPathResource(COMPONENTS_FILE);
            InputStream inputStream = resource.getInputStream();

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            Map<String, Object> componentsData = (Map<String, Object>) data.get("components");

            Map<String, Component> components = new HashMap<>();

            for (Map.Entry<String, Object> entry : componentsData.entrySet()) {
                String componentId = entry.getKey();
                Map<String, Object> componentData = (Map<String, Object>) entry.getValue();

                Component component = parseComponent(componentData);
                components.put(componentId, component);

                log.debug("Loaded component: {} ({})", component.getName(), component.getType());
            }

            log.info("Successfully loaded {} components from YAML", components.size());
            return components;

        } catch (Exception e) {
            log.error("Failed to load components from YAML", e);
            throw new RuntimeException("Failed to load component metadata", e);
        }
    }

    /**
     * Parse a single component from YAML data
     */
    private Component parseComponent(Map<String, Object> data) {
        Component.ComponentBuilder builder = Component.builder();

        // Required fields
        builder.id((String) data.get("id"));
        builder.name((String) data.get("name"));
        builder.description((String) data.get("description"));

        // Parse component type
        String typeString = (String) data.get("type");
        ComponentType type = ComponentType.valueOf(typeString);
        builder.type(type);

        // Parse dependencies (list of strings)
        List<String> dependencies = (List<String>) data.getOrDefault("dependencies", new ArrayList<>());
        builder.dependencies(dependencies);

        // Parse template paths
        List<String> templatePaths = (List<String>) data.getOrDefault("templatePaths", new ArrayList<>());
        builder.templatePaths(templatePaths);

        // Parse documentation paths
        List<String> documentationPaths = (List<String>) data.getOrDefault("documentationPaths", new ArrayList<>());
        builder.documentationPaths(documentationPaths);

        // Parse conflicts (list of component IDs)
        List<String> conflictsList = (List<String>) data.getOrDefault("conflicts", new ArrayList<>());
        builder.conflicts(new HashSet<>(conflictsList));

        // Parse requires (list of component IDs)
        List<String> requiresList = (List<String>) data.getOrDefault("requires", new ArrayList<>());
        builder.requires(new HashSet<>(requiresList));

        return builder.build();
    }
}
