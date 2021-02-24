package com.nined.userservice.dto;

import javax.validation.constraints.NotBlank;

import com.nined.userservice.validator.FieldMatch;
import com.nined.userservice.validator.ValidPassword;

import lombok.Data;

/**
 * Data transfer object for change password functionality
 * 
 * @author vijay
 *
 */
@FieldMatch(first = "newPassword", second = "confirmNewPassword",
        message = "{fieldMatch.changePasswordDto.password}")
@Data
public class ChangePasswordDto {

    @NotBlank(message = "{notBlank.changePasswordDto.newPassword}")
    @ValidPassword(message = "{validPassword.userCredentialDto.password}")
    private String newPassword;

    @NotBlank(message = "{notBlank.changePasswordDto.confirmNewPassword}")
    private String confirmNewPassword;

    @NotBlank(message = "{notBlank.changePasswordDto.oldPassword}")
    private String oldPassword;

}
