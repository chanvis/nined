package com.nined.apigateway.security.userdetails;

import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Object to hold logged in user information
 * 
 * @author vijay
 *
 */
@Data
@RequiredArgsConstructor
public class UserInfo {

    private final Long userId;

    private final List<String> authorities;

    private final String jwtId;
}
