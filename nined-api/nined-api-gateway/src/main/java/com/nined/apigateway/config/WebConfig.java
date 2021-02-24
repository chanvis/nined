package com.nined.apigateway.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nined.apigateway.security.config.JwtConfig;
import com.nined.apigateway.security.resolver.UserHandlerMethodArgumentResolver;

/**
 * Web configurations
 * 
 * @author vijay
 *
 */
@Configuration
@EnableCaching
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * add user info method argument resolver
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserHandlerMethodArgumentResolver(jwtConfig));
    }
}
