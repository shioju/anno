package io.github.anno.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(name = "anno.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AnnoProperties.class)
public class AnnoAutoConfiguration {

    @Bean
    public EndpointDescriptionCollector endpointDescriptionCollector(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        EndpointDescriptionCollector collector =
                new EndpointDescriptionCollector(handlerMapping);
        collector.collect();
        return collector;
    }

    @Bean
    public AnnoEndpointController annoEndpointController(
            EndpointDescriptionCollector collector,
            AnnoProperties properties) {
        return new AnnoEndpointController(collector, properties);
    }
}
