package io.github.anno.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place on a {@code @RequestMapping} handler method to attach a human-readable
 * description. The anno starter will discover it at startup and expose the
 * metadata at a configurable REST endpoint.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EndpointDescription {
    /** The description text for this endpoint. */
    String value();
}
