package com.nined.userservice.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * Data transfer object for User Updates
 * 
 * @author vijay
 *
 */
@Data
public class UpdateUserDto {

    @Email(message = "{email.userRegistrationDto.email}")
    @NotBlank(message = "{notBlank.userRegistrationDto.email}")
    @Size(min = 5, max = 150, message = "{size.userRegistrationDto.email}")
    private String email;

    @NotBlank(message = "{notBlank.userRegistrationDto.firstName}")
    @Size(max = 120, message = "{size.userRegistrationDto.firstName}")
    private String firstName;

    @NotBlank(message = "{notBlank.userRegistrationDto.lastName}")
    @Size(max = 120, message = "{size.userRegistrationDto.lastName}")
    private String lastName;

    @Size(min = 10, max = 18, message = "{size.userRegistrationDto.phone}")
    private String phone;
    
    private boolean active;

}
