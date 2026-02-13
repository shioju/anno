package io.github.anno.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized configuration for the anno starter.
 *
 * <pre>
 *   anno.base-path=/anno       # URL prefix for the metadata endpoint
 *   anno.enabled=true          # kill-switch to disable the feature
 * </pre>
 */
@ConfigurationProperties(prefix = "anno")
public class AnnoProperties {

    private String basePath = "/anno";

    private boolean enabled = true;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
