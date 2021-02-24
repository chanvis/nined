package com.nined.userservice.dto;

import javax.validation.constraints.NotBlank;

import com.nined.userservice.validator.FieldMatch;
import com.nined.userservice.validator.ValidPassword;

import lombok.Data;

/**
 * Data transfer object for reset password functionality
 * 
 * @author vijay
 *
 */
@FieldMatch(first = "password", second = "confirmPassword",
        message = "{fieldMatch.userCredentialDto.password}")
@Data
public class ResetPasswordDto {

    @NotBlank(message = "{notBlank.userCredentialDto.password}")
    @ValidPassword(message = "{validPassword.userCredentialDto.password}")
    private String password;

    @NotBlank(message = "{notBlank.userCredentialDto.confirmPassword}")
    private String confirmPassword;

    @NotBlank(message = "{notBlank.userCredentialDto.token}")
    private String token;

}
