package io.github.anno.autoconfigure;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts typed action annotations into attribute maps for {@link EndpointMetadata}.
 */
final class AnnotationMetadataExtractor {

    static final List<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS = List.of(
            SearchAction.class,
            ReadAction.class,
            ListAction.class,
            CreateAction.class,
            UpdateAction.class,
            DeleteAction.class,
            DownloadAction.class,
            GrantAccessAction.class,
            RevokeAccessAction.class,
            ChangeAccessAction.class
    );

    private AnnotationMetadataExtractor() {}

    /**
     * Extracts a map of attribute key-value pairs from the given annotation.
     * Returns {@code null} if the annotation is not a supported action type.
     */
    static Map<String, String> extract(Annotation annotation) {
        var attrs = new LinkedHashMap<String, String>();

        switch (annotation) {
            case SearchAction a -> {
                attrs.put("action", Action.SEARCH.name());
                attrs.put("condition", a.condition().name());
                attrs.put("query", a.query());
                attrs.put("filters", a.filters());
                attrs.put("fromDate", a.fromDate());
                attrs.put("toDate", a.toDate());
            }
            case ReadAction a -> {
                attrs.put("action", Action.READ.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
                attrs.put("title", a.title());
                attrs.put("category", a.category());
                attrs.put("classification", a.classification());
            }
            case ListAction a -> {
                attrs.put("action", Action.LIST.name());
                attrs.put("condition", a.condition().name());
                attrs.put("title", a.title());
            }
            case CreateAction a -> {
                attrs.put("action", Action.CREATE.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
                attrs.put("title", a.title());
                attrs.put("category", a.category());
                attrs.put("classification", a.classification());
            }
            case UpdateAction a -> {
                attrs.put("action", Action.UPDATE.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
            }
            case DeleteAction a -> {
                attrs.put("action", Action.DELETE.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
            }
            case DownloadAction a -> {
                attrs.put("action", Action.DOWNLOAD.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
                attrs.put("fileName", a.fileName());
            }
            case GrantAccessAction a -> {
                attrs.put("action", Action.GRANT_ACCESS.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
                attrs.put("actorId", a.actorId());
                attrs.put("accessLevel", a.accessLevel());
            }
            case RevokeAccessAction a -> {
                attrs.put("action", Action.REVOKE_ACCESS.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
                attrs.put("actorId", a.actorId());
                attrs.put("accessLevel", a.accessLevel());
            }
            case ChangeAccessAction a -> {
                attrs.put("action", Action.CHANGE_ACCESS.name());
                attrs.put("condition", a.condition().name());
                attrs.put("resourceId", a.resourceId());
                attrs.put("actorId", a.actorId());
                attrs.put("accessLevel", a.accessLevel());
            }
            default -> {
                return null;
            }
        }

        return Collections.unmodifiableMap(attrs);
    }
}
