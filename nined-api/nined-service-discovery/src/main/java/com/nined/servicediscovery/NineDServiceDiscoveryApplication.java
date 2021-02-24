package com.nined.servicediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class NineDServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(NineDServiceDiscoveryApplication.class, args);
    }

}
