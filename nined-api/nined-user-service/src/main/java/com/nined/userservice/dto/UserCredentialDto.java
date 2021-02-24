package com.nined.userservice.dto;

import javax.validation.constraints.NotBlank;

import com.nined.userservice.validator.FieldMatch;
import com.nined.userservice.validator.ValidPassword;
import com.nined.userservice.validator.ValidUsername;

import lombok.Data;

/**
 * Data transfer object for setting user credential functionality
 * 
 * @author vijay
 *
 */
@FieldMatch(first = "password", second = "confirmPassword",
        message = "{fieldMatch.userCredentialDto.password}")
@Data
public class UserCredentialDto {

    @ValidPassword(message = "{validPassword.userCredentialDto.password}")
    @NotBlank(message = "{notBlank.userCredentialDto.password}")
    private String password;

    @NotBlank(message = "{notBlank.userCredentialDto.confirmPassword}")
    private String confirmPassword;

    @ValidUsername(message = "{validUsername.userCredentialDto.username}")
    @NotBlank(message = "{notBlank.userCredentialDto.username}")
    private String username;

    @NotBlank(message = "{notBlank.userCredentialDto.token}")
    private String token;
}
