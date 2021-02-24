package com.nined.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nined.apigateway.constants.ApiGatewayConstants;
import com.nined.apigateway.security.userdetails.UserInfo;
import com.nined.apigateway.security.userdetails.WithUser;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller handle JWT revoke request
 * 
 */
@Slf4j
@RestController
public class JwtRevokeController {

    @Autowired
    private CacheManager cacheManager;

    /**
     * API to add jwt id to blacklist on JWT revoke request
     * 
     * @param userInfo
     * @return
     */
    @PostMapping("/api/service/user/token/revoke")
    public ResponseEntity<?> jwtRevoke(@WithUser UserInfo userInfo) {
        if (userInfo.getUserId() != null) {
            if (log.isInfoEnabled()) {
                log.info("Revoking token for user id {}", userInfo.getUserId());
            }
            Cache cache = cacheManager.getCache(ApiGatewayConstants.JWT_BLACKLIST_CACHENAME);
            if (cache != null) {
                // blacklisting user and jwt id
                cache.put(userInfo.getUserId(), userInfo.getJwtId());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
