package com.nined.apigateway.config.swagger;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger UI configuration
 * 
 */
@EnableSwagger2
@Configuration
public class SwaggerConfiguration {
    @Bean
    public RestTemplate configureTempalte() {
        return new RestTemplate();
    }

    /**
     * Reads the swagger-api JSON files from ServiceDefinitionsContext
     * 
     * @param defaultResourcesProvider
     * @param restTemplate
     * @param definitionContext
     * @return
     */
    @Primary
    @Bean
    @Lazy
    public SwaggerResourcesProvider swaggerResourcesProvider(
            InMemorySwaggerResourcesProvider defaultResourcesProvider, RestTemplate restTemplate,
            ServiceDefinitionsContext definitionContext) {
        return () -> {
            List<SwaggerResource> resources = new ArrayList<>(defaultResourcesProvider.get());
            resources.clear();
            resources.addAll(definitionContext.getSwaggerDefinitions());
            return resources;
        };
    }
}
