package io.github.anno.autoconfigure;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.PathContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serves collected endpoint metadata.
 *
 * <ul>
 *   <li>{@code GET {basePath}} — returns ALL annotated endpoints</li>
 *   <li>{@code GET {basePath}/{path}} — returns metadata for a specific endpoint path</li>
 *   <li>{@code GET {basePath}/{path}?method=GET} — filters to a specific HTTP method</li>
 * </ul>
 */
@RestController
public class AnnoEndpointController {

    private final EndpointDescriptionCollector collector;
    private final AnnoProperties properties;

    public AnnoEndpointController(EndpointDescriptionCollector collector,
                                  AnnoProperties properties) {
        this.collector = collector;
        this.properties = properties;
    }

    @GetMapping("${anno.base-path:/anno}/**")
    public ResponseEntity<?> getEndpointMetadata(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String basePath = normalizeBasePath(properties.getBasePath());

        String targetPath = requestUri.substring(basePath.length());

        if (targetPath.isEmpty() || targetPath.equals("/")) {
            Map<String, List<EndpointSummary>> summary = new LinkedHashMap<>();
            collector.getAllMetadata().forEach((path, entries) ->
                summary.put(path, entries.stream()
                        .map(m -> new EndpointSummary(m.method(), m.attributes()))
                        .collect(Collectors.toList()))
            );
            return ResponseEntity.ok(summary);
        }

        if (!targetPath.startsWith("/")) {
            targetPath = "/" + targetPath;
        }

        List<EndpointMetadata> metadata = collector.getMetadata(targetPath);
        Map<String, String> pathParameters = Map.of();

        if (metadata == null) {
            PathPatternParser parser = new PathPatternParser();
            PathContainer pathContainer = PathContainer.parsePath(targetPath);
            for (Map.Entry<String, List<EndpointMetadata>> entry : collector.getAllMetadata().entrySet()) {
                PathPattern pattern = parser.parse(entry.getKey());
                PathPattern.PathMatchInfo matchInfo = pattern.matchAndExtract(pathContainer);
                if (matchInfo != null) {
                    metadata = entry.getValue();
                    pathParameters = matchInfo.getUriVariables();
                    break;
                }
            }
        }

        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }

        String methodFilter = request.getParameter("method");
        if (methodFilter != null && !methodFilter.isBlank()) {
            String upper = methodFilter.toUpperCase();
            metadata = metadata.stream()
                    .filter(m -> m.method().equals(upper) || m.method().equals("ALL"))
                    .collect(Collectors.toList());
            if (metadata.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.ok(new EndpointResponse(pathParameters, metadata));
    }

    private String normalizeBasePath(String basePath) {
        if (basePath == null || basePath.isBlank()) {
            return "/anno";
        }
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        return basePath;
    }

    private record EndpointSummary(String method, Map<String, String> attributes) {}

    private record EndpointResponse(Map<String, String> pathParameters, List<EndpointMetadata> endpoints) {}
}
