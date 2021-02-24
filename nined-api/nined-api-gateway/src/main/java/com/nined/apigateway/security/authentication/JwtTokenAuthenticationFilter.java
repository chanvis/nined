package com.nined.apigateway.security.authentication;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nined.apigateway.constants.ApiGatewayConstants;
import com.nined.apigateway.security.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter class to authenticate JWT token
 * 
 * @author vijay
 *
 */
@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;
    private final CacheManager cacheManager;

    public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, CacheManager cacheManager) {
        this.jwtConfig = jwtConfig;
        this.cacheManager = cacheManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        // 1. get the authentication header.
        String header = request.getHeader(jwtConfig.getHeader());
        // 2. validate the header and check the prefix
        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            chain.doFilter(request, response); // If not valid, go to the next filter.
            log.error("header not valid");
            return;
        }
        // 3. Get the token
        String token = header.replace(jwtConfig.getPrefix(), ApiGatewayConstants.BLANK_STR);
        try {
            // 4. Validate the token
            Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes())
                    .parseClaimsJws(token).getBody();

            String logonId = claims.getSubject();
            if (logonId != null) {
                // 5. If exists get the token id from cache for the given user id
                ValueWrapper cacheValue =
                        cacheManager.getCache(ApiGatewayConstants.JWT_BLACKLIST_CACHENAME).get(
                                ((Number) claims.get(ApiGatewayConstants.USER_ID)).longValue());
                if (cacheValue != null) {
                    String jwtId = cacheValue.get().toString();

                    // 6. check if token id is blacklisted
                    if (jwtId != null && jwtId.equals(claims.getId())) {
                        throw new BlacklistedTokenException("Blacklisted token");
                    }
                }

                @SuppressWarnings("unchecked")
                List<String> authorities =
                        (List<String>) claims.get(ApiGatewayConstants.AUTHORITIES);
                
                // 7. Create auth object
                UsernamePasswordAuthenticationToken auth =  
                		new UsernamePasswordAuthenticationToken(logonId, null, authorities.stream()
                				.map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            	
            	// 8. Authenticate the user
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
        	log.error("User Authentication Failed ");
            // In case of failure.
            SecurityContextHolder.clearContext();
        }

        // go to the next filter in the filter chain
        chain.doFilter(request, response);
    }
}
