package com.nined.apigateway.config.swagger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * This component is responsible for reading all the registered service instances on Eureka server,
 * polls them for the Swagger definition, and stores them in ServiceDefinitionsContext
 * 
 */
@Slf4j
@Component
public class ServiceDescriptionUpdater {

    private static final String DEFAULT_SWAGGER_URL = "/v2/api-docs";
    private static final String KEY_SWAGGER_URL = "swagger_url";

    @Value("${spring.application.name:nined-api-gateway}")
    private String apiGatewayAppName;

    @Value("${swagger.config.enable:true}")
    private boolean swaggerEnabled;

    @Autowired
    private DiscoveryClient discoveryClient;

    private final RestTemplate template;

    public ServiceDescriptionUpdater() {
        this.template = new RestTemplate();
    }

    @Autowired
    private ServiceDefinitionsContext definitionContext;

    @Scheduled(fixedDelayString = "${swagger.config.refreshrate}", initialDelayString = "60000")
    public void refreshSwaggerConfigurations() {
        if (swaggerEnabled) {
            if (log.isDebugEnabled()) {
                log.debug("Starting Service Definition Context refresh");
            }

            discoveryClient.getServices().stream()
                    .filter(serviceId -> !serviceId.equals(apiGatewayAppName))
                    .forEach(serviceId -> {
                        if (log.isInfoEnabled()) {
                            log.info("Attempting service definition refresh for Service : {} ",
                                    serviceId);
                        }
                        List<ServiceInstance> serviceInstances =
                                discoveryClient.getInstances(serviceId);
                        if (serviceInstances == null || serviceInstances.isEmpty()) {
                            if (log.isInfoEnabled()) {
                                log.info("No instances available for service : {} ", serviceId);
                            }
                        } else {
                            ServiceInstance instance = serviceInstances.get(0);
                            String swaggerURL = getSwaggerURL(instance);

                            Optional<Object> jsonData =
                                    getSwaggerDefinitionForAPI(serviceId, swaggerURL);

                            if (jsonData.isPresent()) {
                                String content = getJSON(serviceId, jsonData.get());
                                definitionContext.addServiceDefinition(serviceId, content);
                            } else {
                                if (log.isErrorEnabled()) {
                                    log.error(
                                            "Skipping service id : {} Error : Could not get Swagegr definition from API ",
                                            serviceId);
                                }
                            }
                            if (log.isInfoEnabled()) {
                                log.info("Service Definition Context Refreshed at :  {} {}",
                                        LocalDate.now(), LocalTime.now());
                            }
                        }
                    });
        }
    }

    private String getSwaggerURL(ServiceInstance instance) {
        String swaggerURL = instance.getMetadata().get(KEY_SWAGGER_URL);
        return swaggerURL != null ? instance.getUri() + swaggerURL
                : instance.getUri() + DEFAULT_SWAGGER_URL;
    }

    private Optional<Object> getSwaggerDefinitionForAPI(String serviceName, String url) {
        if (log.isDebugEnabled()) {
            log.debug("Accessing the SwaggerDefinition JSON for Service : {} : URL : {} ",
                    serviceName, url);
        }
        try {
            Object jsonData = template.getForObject(url, Object.class);
            return Optional.of(jsonData);
        } catch (RestClientException ex) {
            if (log.isErrorEnabled()) {
                log.error("Error while getting service definition for service : {} Error : {} ",
                        serviceName, ex.getMessage());
            }
            return Optional.empty();
        }

    }

    public String getJSON(String serviceId, Object jsonData) {
        try {
            return new ObjectMapper().writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            if (log.isErrorEnabled()) {
                log.error("Error occurred while getting JSON for service {}", serviceId);
                log.error("Error : {} ", e.getMessage());
            }
            return "";
        }
    }
}
