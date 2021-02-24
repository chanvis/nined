package com.nined.apigateway.security.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

/**
 * API configuration
 * 
 * @author vijay
 *
 */
@Getter
public class ApiConfig {

    @Value("${user.service.verification.code.uri}")
    private String userVerificationCodeUri;

    @Value("${user.service.credential.uri}")
    private String userCredentialUri;

    @Value("${user.service.user.uri}")
    private String userUri;

    @Value("${user.service.user.id.uri}")
    private String userIdUri;
    
    @Value("${user.service.confirm-email.uri}")
    private String confirmEmailUri;
    
    @Value("${user.service.user.add.uri}")
    private String addUserUri;    

    @Value("${user.service.forgot-password.uri}")
    private String forgotPasswordUri;

    @Value("${user.service.reset-password.uri}")
    private String resetPaswordUri;

    @Value("${user.service.refresh-auth.uri}")
    private String refreshAuthURI;    
    
}
