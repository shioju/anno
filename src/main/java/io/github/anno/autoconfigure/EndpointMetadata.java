package io.github.anno.autoconfigure;

import java.util.Set;

/**
 * Immutable record holding the metadata collected from one annotated handler method.
 * Serialized directly to JSON by Jackson when returned from the anno controller.
 *
 * @param methods     HTTP methods (GET, POST, etc.)
 * @param path        the resolved URL path (e.g. "/user/{id}")
 * @param description the value from {@link EndpointDescription}
 */
public record EndpointMetadata(
        Set<String> methods,
        String path,
        String description
) {}
