package io.github.anno.autoconfigure;

import java.util.Map;

/**
 * Immutable record holding the metadata collected from one annotated handler method.
 * Serialized directly to JSON by Jackson when returned from the anno controller.
 *
 * @param method     HTTP method (GET, POST, etc.)
 * @param attributes key-value pairs extracted from typed endpoint annotations
 */
public record EndpointMetadata(
        String method,
        Map<String, String> attributes
) {}
