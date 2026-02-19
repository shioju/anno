package io.github.anno.autoconfigure;

/**
 * Immutable record holding the metadata collected from one annotated handler method.
 * Serialized directly to JSON by Jackson when returned from the anno controller.
 *
 * @param method      HTTP method (GET, POST, etc.)
 * @param description the value from {@link EndpointDescription}
 */
public record EndpointMetadata(
        String method,
        String description
) {}
