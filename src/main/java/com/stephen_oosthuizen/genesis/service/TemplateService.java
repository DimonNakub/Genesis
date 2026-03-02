package com.stephen_oosthuizen.genesis.service;

import com.stephen_oosthuizen.genesis.exception.TemplateNotFoundException;
import com.stephen_oosthuizen.genesis.model.TemplateContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for loading and rendering FreeMarker templates.
 * Handles template caching and context processing.
 */
@Service
@Slf4j
public class TemplateService {
    private final Configuration freemarkerConfig;

    @Autowired
    public TemplateService(FreeMarkerConfigurer configurer) {
        this.freemarkerConfig = configurer.getConfiguration();
    }

    /**
     * Render a template with the given context
     * Templates are cached for performance
     *
     * @param templatePath Path to the template file (e.g., "web/flask/app.py.ftl")
     * @param context Template context with variables
     * @return Rendered template content
     */
    @Cacheable(value = "templates", key = "#templatePath")
    public String renderTemplate(String templatePath, TemplateContext context) {
        log.debug("Rendering template: {} (cache miss)", templatePath);

        try {
            // Load template
            Template template = freemarkerConfig.getTemplate(templatePath);

            // Convert context to Map for FreeMarker
            Map<String, Object> dataModel = buildDataModel(context);

            // Render template
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);

            String result = writer.toString();
            log.debug("Template {} rendered successfully ({} characters)", templatePath, result.length());
            return result;

        } catch (IOException e) {
            log.error("Template not found: {}", templatePath, e);
            throw new TemplateNotFoundException("Template not found: " + templatePath, e);
        } catch (TemplateException e) {
            log.error("Error processing template: {}", templatePath, e);
            throw new TemplateNotFoundException("Error processing template: " + templatePath, e);
        }
    }

    /**
     * Build FreeMarker data model from TemplateContext
     */
    private Map<String, Object> buildDataModel(TemplateContext context) {
        Map<String, Object> model = new HashMap<>();

        // Add all context properties
        model.put("projectName", context.getProjectName());
        model.put("description", context.getDescription());
        model.put("pythonVersion", context.getPythonVersion());
        model.put("authorName", context.getAuthorName());
        model.put("authorEmail", context.getAuthorEmail());

        // Convert components to FreeMarker-friendly maps
        java.util.List<Map<String, Object>> componentMaps = new java.util.ArrayList<>();
        for (var component : context.getComponents()) {
            Map<String, Object> compMap = new HashMap<>();
            compMap.put("id", component.getId());
            compMap.put("name", component.getName());
            compMap.put("description", component.getDescription());
            compMap.put("typeName", component.getType().name());
            compMap.put("typeDisplayName", component.getType().getDisplayName());
            compMap.put("dependencies", component.getDependencies());
            componentMaps.add(compMap);
        }
        model.put("components", componentMaps);

        // Add helper methods
        model.put("hasDatabase", context.hasDatabase());
        model.put("hasWebFramework", context.hasWebFramework());
        model.put("hasSecurity", context.hasSecurity());
        model.put("hasGui", context.hasGui());
        model.put("hasTesting", context.hasTesting());
        model.put("hasDocker", context.hasDocker());
        model.put("mainFramework", context.getMainFramework());
        model.put("currentDate", context.getCurrentDate());
        model.put("currentYear", context.getCurrentYear());
        model.put("allDependencies", context.getAllDependencies());

        // Add helper function for checking specific components
        // Create a map of component IDs for easy checking in templates
        Map<String, Boolean> componentFlags = new HashMap<>();
        for (var component : context.getComponents()) {
            componentFlags.put("has_" + component.getId(), true);
        }
        model.putAll(componentFlags);

        // Add custom variables
        model.putAll(context.getCustomVariables());

        return model;
    }

    /**
     * Check if a template exists
     */
    public boolean templateExists(String templatePath) {
        try {
            freemarkerConfig.getTemplate(templatePath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
