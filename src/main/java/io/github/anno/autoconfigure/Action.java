package io.github.anno.autoconfigure;

/**
 * The type of action an endpoint performs.
 */
public enum Action {
    SEARCH,
    READ,
    LIST,
    CREATE,
    UPDATE,
    DELETE,
    DOWNLOAD,
    GRANT_ACCESS,
    REVOKE_ACCESS,
    CHANGE_ACCESS
}
