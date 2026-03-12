package io.github.anno.autoconfigure;

/**
 * The condition under which an endpoint action applies.
 */
public enum Condition {
    IF_AVAILABLE,
    SUCCESSFUL,
    FAILED,
    ALWAYS
}
