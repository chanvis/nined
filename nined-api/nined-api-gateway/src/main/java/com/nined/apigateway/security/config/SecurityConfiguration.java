package com.nined.apigateway.security.config;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nined.apigateway.constants.ApiGatewayConstants;
import com.nined.apigateway.enums.Privilege;
import com.nined.apigateway.security.authentication.JwtTokenAuthenticationFilter;

/**
 * Security configuration class
 * 
 * @author vijay
 *
 */
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private CacheManager cacheManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // make sure we use stateless session; session won't be used to store user's
                // state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // handle an authorized attempts
                .exceptionHandling()
                .authenticationEntryPoint(
                        (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                ApiGatewayConstants.INVALID_EXPIRED_JWT))
                .and()
                // Add a filter to validate the tokens with every request
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig, cacheManager),
                        UsernamePasswordAuthenticationFilter.class)
                // authorization requests config
                .authorizeRequests()
                .antMatchers("/actuator/**", "/v2/api-docs", "/configuration/**", "/swagger*/**",
                        "/webjars/**", "/service/**","/**")
                .permitAll() //
                .antMatchers(HttpMethod.POST, jwtConfig.getUri(), apiConfig.getForgotPasswordUri(),
                		apiConfig.getAddUserUri())
                .permitAll() //
                .antMatchers(HttpMethod.PUT, apiConfig.getUserCredentialUri(),apiConfig.getUserVerificationCodeUri(),
                        apiConfig.getResetPaswordUri(), apiConfig.getConfirmEmailUri())
                .permitAll() //

                .antMatchers(HttpMethod.POST, apiConfig.getRefreshAuthURI())
                .hasAuthority(Privilege.REFRESH_AUTH.id()) //
                
                // USERS
                .antMatchers(HttpMethod.PATCH, apiConfig.getUserIdUri())
                .hasAnyAuthority(Privilege.ALL_USER.id(), Privilege.EDIT_USER.id()) //

                .antMatchers(HttpMethod.PUT, apiConfig.getUserIdUri())
                .hasAnyAuthority(Privilege.ALL_USER.id(), Privilege.EDIT_USER.id()) //

                .antMatchers(HttpMethod.DELETE, apiConfig.getUserIdUri())
                .hasAnyAuthority(Privilege.ALL_USER.id(), Privilege.DELETE_USER.id()) //

                .antMatchers(HttpMethod.GET, apiConfig.getUserUri())
                .hasAnyAuthority(Privilege.ALL_USER.id(), Privilege.VIEW_USER.id()) //

                // any other requests must be authenticated
                .anyRequest().authenticated();
    }

}
