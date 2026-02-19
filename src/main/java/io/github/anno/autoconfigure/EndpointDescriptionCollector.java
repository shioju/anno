package io.github.anno.autoconfigure;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Scans all registered handler methods via {@link RequestMappingHandlerMapping},
 * finds those annotated with {@link EndpointDescription}, and builds an
 * in-memory index keyed by path.
 */
public class EndpointDescriptionCollector {

    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<String, List<EndpointMetadata>> metadataByPath = new ConcurrentHashMap<>();

    public EndpointDescriptionCollector(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Iterates every registered handler method and collects those carrying
     * {@link EndpointDescription}.
     */
    public void collect() {
        Map<RequestMappingInfo, HandlerMethod> methods = handlerMapping.getHandlerMethods();

        for (var entry : methods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            EndpointDescription annotation =
                    handlerMethod.getMethodAnnotation(EndpointDescription.class);
            if (annotation == null) {
                continue;
            }

            RequestMappingInfo mappingInfo = entry.getKey();
            Set<String> patterns = mappingInfo.getPatternValues();

            Set<String> httpMethods = mappingInfo.getMethodsCondition()
                    .getMethods()
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet());

            String description = annotation.value();

            for (String pattern : patterns) {
                metadataByPath.computeIfAbsent(pattern, k -> new ArrayList<>())
                        .add(new EndpointMetadata(
                                httpMethods.isEmpty() ? Set.of("ALL") : httpMethods,
                                pattern,
                                description
                        ));
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
