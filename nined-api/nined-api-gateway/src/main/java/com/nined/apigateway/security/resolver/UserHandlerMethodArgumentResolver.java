package com.nined.apigateway.security.resolver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.nined.apigateway.constants.ApiGatewayConstants;
import com.nined.apigateway.security.config.JwtConfig;
import com.nined.apigateway.security.userdetails.UserInfo;
import com.nined.apigateway.security.userdetails.WithUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Custom method argument resolver class to create user info object using JWT token
 * 
 * @author vijay
 */
public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtConfig jwtConfig;

    public UserHandlerMethodArgumentResolver(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WithUser.class)
                && parameter.getParameterType().equals(UserInfo.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // 1. take the request
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        // 2. get the authentication header.
        String authorizationHeader = httpServletRequest.getHeader(jwtConfig.getHeader());

        // 3. validate the header and check the prefix
        if (authorizationHeader == null || !authorizationHeader.startsWith(jwtConfig.getPrefix())) {
            return new UserInfo(null, null, null);
        }
        // 4. Get the token
        String token = authorizationHeader.replace(jwtConfig.getPrefix(), "");
        // 5. Using jwt library parse the token and create Claim object
        Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(token).getBody();

        if (claims != null) {
            // 6. create UserInfo object
            return new UserInfo(claims.get(ApiGatewayConstants.USER_ID, Long.class),
                    claims.get(ApiGatewayConstants.AUTHORITIES, List.class), claims.getId());
        }
        return new UserInfo(null, null, null);
    }
}
