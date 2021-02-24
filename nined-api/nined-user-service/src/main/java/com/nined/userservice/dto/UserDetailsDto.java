package com.nined.userservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Data transfer object for client admin details
 * 
 * @author vijay
 *
 */
@Data
@RequiredArgsConstructor
public class UserDetailsDto {

    private final Long userId;

    private final String logonId;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String phone;
}
