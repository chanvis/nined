package com.nined.userservice.dto;

import javax.validation.constraints.NotNull;

import com.nined.userservice.validator.ValidRole;

import lombok.Data;

/**
 * Data transfer object for feature data
 * 
 * @author vijay
 *
 */
@Data
public class RoleDto {

    @ValidRole(message = "validRole.roleDto.roleId")
    @NotNull(message = "notNull.userRegistrationDto.roleId")
    private Long roleId;

    private String name;

    private String description;

    private boolean active;

    private Long createdDate;

    private Long lastUpdatedDate;
}
