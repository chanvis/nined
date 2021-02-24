package com.nined.apigateway.constants;

/**
 * @author vijay
 *
 */
public class ApiGatewayConstants {

    private ApiGatewayConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String BLANK_STR = "";

    // security constants
    public static final String AUTHORITIES = "authorities";
    public static final String USER_ID = "userId";
    public static final String INVALID_EXPIRED_JWT = "Invalid or expired token";

    public static final String JWT_BLACKLIST_CACHENAME = "jwtBlacklistCache";
}
