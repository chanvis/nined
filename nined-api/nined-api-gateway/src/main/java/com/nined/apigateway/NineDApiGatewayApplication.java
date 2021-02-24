package com.nined.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import com.nined.apigateway.security.config.ApiConfig;
import com.nined.apigateway.security.config.JwtConfig;

@EnableScheduling
@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class NineDApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(NineDApiGatewayApplication.class, args);
    }

    /**
     * JWT configuration bean
     * 
     * @return
     */
    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    /**
     * API uri configuration bean
     * 
     * @return
     */
    @Bean
    public ApiConfig apiConfig() {
        return new ApiConfig();
    }

    /**
     * Use DefaultHttpFirewall instead of StrictHttpFirewall to handle RequestRejectedException on
     * Spring Boot Actuator end-point //env
     * 
     * @return
     */
    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}
