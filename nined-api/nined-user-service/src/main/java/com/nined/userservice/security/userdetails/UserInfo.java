package com.nined.userservice.security.userdetails;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Object to hold logged in user information
 * 
 * @author vijay
 *
 */
@Getter
@RequiredArgsConstructor
public class UserInfo {

    @ApiModelProperty(required = false, hidden = true)
    private final String logonId;

    @ApiModelProperty(required = false, hidden = true)
    private final Long userId;

    @ApiModelProperty(required = false, hidden = true)
    private final List<String> authorities;

    @ApiModelProperty(required = false, hidden = true)
    private final Long storeId;

    @ApiModelProperty(required = false, hidden = true)
    private final Long orgId;

    @ApiModelProperty(required = false, hidden = true)
    private final String role;

    @ApiModelProperty(required = false, hidden = true)
    private final String lang;
}
