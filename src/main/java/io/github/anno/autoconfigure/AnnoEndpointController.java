package io.github.anno.autoconfigure;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Serves collected {@link EndpointDescription} metadata.
 *
 * <ul>
 *   <li>{@code GET {basePath}} — returns ALL annotated endpoints</li>
 *   <li>{@code GET {basePath}/{path}} — returns metadata for a specific endpoint path</li>
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
            List<EndpointMetadata> all = collector.getAllMetadata();
            return ResponseEntity.ok(all);
        }

        if (!targetPath.startsWith("/")) {
            targetPath = "/" + targetPath;
        }

        EndpointMetadata metadata = collector.getMetadata(targetPath);
        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(metadata);
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
}
