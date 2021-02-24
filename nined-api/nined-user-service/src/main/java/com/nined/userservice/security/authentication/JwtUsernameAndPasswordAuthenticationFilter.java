package com.nined.userservice.security.authentication;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nined.userservice.constants.UserConstants;
import com.nined.userservice.enums.Privilege;
import com.nined.userservice.security.config.JwtConfig;
import com.nined.userservice.security.userdetails.User;
import com.nined.userservice.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom username and password authentication filter
 * <li>perform authentication using authentication manager</li>
 * <li>upon successful authentication, generate a token</li>
 * 
 * @author vijay
 *
 */
@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {
    private static final String ERROR_MESSAGE =
            "Something went wrong while parsing /user/auth request body";

    private AuthenticationManager authManager;

    private final JwtConfig jwtConfig;

    private UserService userService;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager,
            JwtConfig jwtConfig, UserService userService) {
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;
        this.userService = userService;

        // override the defaults to use /user/auth
        this.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher(jwtConfig.getUri(), HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) {
        try {
        	log.info("attempt Authentication --> ");
            // 1. Get credentials from the request
            UserCredentials creds =
                    new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);

            // 2. Create auth token object which will be used by auth manager
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    creds.getLogonId(), creds.getLogonPassword(), Collections.emptyList());
         
            // 3. Authentication manager authenticate the user, and use
            // UserDetialsServiceImpl::loadUserByUsername() method to load the user.
            return authManager.authenticate(authToken);

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(ERROR_MESSAGE, e);
            }
            throw new InternalAuthenticationServiceException(ERROR_MESSAGE, e);
        }
    }

    /**
     * Upon successful authentication, generate a token.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication auth)
            throws IOException, ServletException {
        Long now = System.currentTimeMillis();
        User authenticatedUser = (User) auth.getPrincipal();
        int tokenExpiry = authenticatedUser.isTwoFAEnabled() ? jwtConfig.getPreAuthExpiration()
                : jwtConfig.getExpiration();
        log.info("on successful Authentication  --> Generating token --> "); 

        // construct auth token
        String token = Jwts.builder().setSubject(auth.getName())
                .claim(UserConstants.AUTHORITIES,
                        auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .claim(UserConstants.USER_ID, authenticatedUser.getUserId())
                .claim(UserConstants.ROLE, authenticatedUser.getRole())
                .claim(UserConstants.LANG, authenticatedUser.getLang()) //
                .setIssuedAt(new Date(now)).setExpiration(new Date(now + tokenExpiry * 1000)) // in
                                                                                              // milliseconds
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes()).compact();

        // construct refresh token
        final List<String> refreshAuthAuthorities = new ArrayList<>();
        refreshAuthAuthorities.add(Privilege.REFRESH_AUTH.id());
        String refreshtoken = Jwts.builder().setSubject(auth.getName())
                .claim(UserConstants.AUTHORITIES,refreshAuthAuthorities)
                .claim(UserConstants.USER_ID, authenticatedUser.getUserId())
                .claim(UserConstants.ROLE, authenticatedUser.getRole())
                .claim(UserConstants.LANG, authenticatedUser.getLang()) //
                .setIssuedAt(new Date(now)).setExpiration(new Date(now +  jwtConfig.getRefreshExpiration() * 1000)) // in
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes()).compact();
        
        
        // Add token to header
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
        if (authenticatedUser.isTwoFAEnabled()) {
            // generate and send user verification code
            userService.generateAndSendVerificationToken(authenticatedUser.getUsername(),request);
        } else {
        	// Add refresh token to header
            response.addHeader(jwtConfig.getRefreshToken(), jwtConfig.getPrefix() + refreshtoken);
        }
    }

    /**
     * A class to represent the user credentials
     * 
     */
    @Getter
    @Setter
    @SuppressWarnings("unused")
    private static class UserCredentials {
        private String logonId;
        private String logonPassword;
    }
}
