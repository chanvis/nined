package com.nined.userservice.dto;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for user verification code functionality
 * 
 * @author vijay
 *
 */
@Getter
@Setter
public class UserVerificationCodeDto {

    @JsonProperty("code")
    @NotNull(message = "{notNull.userVerificationCodeDto.verificationCode}")
    private Integer verificationCode;
    
    @JsonProperty("emailId")
    @NotNull(message = "{notNull.userVerificationCodeDto.emailId}")
    private String emailId;    
}
