package com.nined.userservice.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configuration class for API documentation
 * 
 * @author vijay
 *
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.nined.userservice.controller"))
                .paths(PathSelectors.any()).build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("NineD User Services",
                "Services to perform operation on user, client and organization entities",
                getVersion(), "Terms of Service", new Contact("", "", ""), "License of API", "",
                Collections.emptyList());
    }

    private String getVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            // we could not get the version, so use a blank
            version = "";
        }
        return version;
    }
}
