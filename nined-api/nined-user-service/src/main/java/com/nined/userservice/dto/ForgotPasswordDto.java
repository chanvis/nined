package com.nined.userservice.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data transfer object for forgot password functionality
 * 
 * @author vijay
 *
 */
@Data
public class ForgotPasswordDto {

    @NotBlank(message = "{notBlank.forgotPasswordDto.username}")
    private String username;
}
