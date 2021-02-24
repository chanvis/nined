package com.nined.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.nined.apigateway.config.swagger.ServiceDefinitionsContext;

/**
 * Controller to serve the JSON from our in-memory store
 * 
 */
@RestController
public class ServiceDefinitionController {

    @Autowired
    private ServiceDefinitionsContext definitionContext;

    /**
     * Return JSON from ServiceDefinitionsContext as a response.
     * 
     * @param serviceName
     * @return
     */
    @GetMapping("/service/{servicename}")
    public String getServiceDefinition(@PathVariable("servicename") String serviceName) {
        return definitionContext.getSwaggerDefinition(serviceName);
    }
}
