package com.nined.userservice.security.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

/**
 * JWT configuration
 * 
 * @author vijay
 *
 */
@Getter
public class JwtConfig {

    @Value("${security.jwt.uri:/user/auth/**}")
    private String uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.header:Refresh}")
    private String refreshToken;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration:#{1*60*60}}")
    private int expiration;

    @Value("${security.jwt.refreshExpiration:#{6*60*60}}")
    private int refreshExpiration;
    
    @Value("${security.jwt.preauth.expiration:#{0.25*60*60}}")
    private int preAuthExpiration;

    @Value("${security.jwt.secret:UserAppSecretKey}")
    private String secret;

}
