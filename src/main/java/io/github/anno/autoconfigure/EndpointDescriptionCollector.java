package io.github.anno.autoconfigure;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Scans all registered handler methods via {@link RequestMappingHandlerMapping},
 * finds those annotated with typed action annotations (e.g. {@link SearchAction},
 * {@link ReadAction}), and builds an in-memory index keyed by path.
 */
public class EndpointDescriptionCollector {

    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<String, List<EndpointMetadata>> metadataByPath = new ConcurrentHashMap<>();

    public EndpointDescriptionCollector(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Iterates every registered handler method and collects those carrying
     * typed action annotations.
     */
    public void collect() {
        Map<RequestMappingInfo, HandlerMethod> methods = handlerMapping.getHandlerMethods();

        for (var entry : methods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo mappingInfo = entry.getKey();

            List<Map<String, String>> extractedAttributes = new ArrayList<>();

            for (Class<? extends Annotation> annoType : AnnotationMetadataExtractor.SUPPORTED_ANNOTATIONS) {
                Annotation[] annotations = handlerMethod.getMethod().getAnnotationsByType(annoType);
                for (Annotation annotation : annotations) {
                    Map<String, String> attrs = AnnotationMetadataExtractor.extract(annotation);
                    if (attrs != null) {
                        extractedAttributes.add(attrs);
                    }
                }
            }

            if (extractedAttributes.isEmpty()) {
                continue;
            }

            Set<String> patterns = mappingInfo.getPatternValues();

            Set<String> httpMethods = mappingInfo.getMethodsCondition()
                    .getMethods()
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet());

            String method = httpMethods.isEmpty() ? "ALL" : httpMethods.iterator().next();

            for (Map<String, String> attrs : extractedAttributes) {
                for (String pattern : patterns) {
                    metadataByPath.computeIfAbsent(pattern, k -> new ArrayList<>())
                            .add(new EndpointMetadata(method, attrs));
                }
            }
        }
    }

    public List<EndpointMetadata> getMetadata(String path) {
        return metadataByPath.get(path);
    }

    public Map<String, List<EndpointMetadata>> getAllMetadata() {
        return Collections.unmodifiableMap(metadataByPath);
    }
}
