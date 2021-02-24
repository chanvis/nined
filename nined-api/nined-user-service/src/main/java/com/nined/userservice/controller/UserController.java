package com.nined.userservice.controller;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nined.userservice.constants.UserConstants;
import com.nined.userservice.dto.ChangePasswordDto;
import com.nined.userservice.dto.ForgotPasswordDto;
import com.nined.userservice.dto.GenericResponse;
import com.nined.userservice.dto.ResetPasswordDto;
import com.nined.userservice.dto.UpdateUserDto;
import com.nined.userservice.dto.UserCredentialDto;
import com.nined.userservice.dto.UserDto;
import com.nined.userservice.dto.UserRegistrationDto;
import com.nined.userservice.dto.UserVerificationCodeDto;
import com.nined.userservice.model.JPAPasswordResetToken;
import com.nined.userservice.model.JPAUser;
import com.nined.userservice.security.userdetails.UserInfo;
import com.nined.userservice.security.userdetails.WithUser;
import com.nined.userservice.service.UserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Rest controller class responsible for hosting end-points and methods related to User entity
 * 
 * @author vijay
 *
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    /**
     * PUT method to resend verification code
     * 
     * @param userInfo
     * @return
     */
    @ApiOperation(value = "Verification code resend API")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "Verification code resent successfully")})
    @PutMapping(value = "/account/code/resend")
    public ResponseEntity<GenericResponse> resendVerificationCode(@RequestBody @Valid final ForgotPasswordDto forgotPasswordDto,
    		final HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Resend verification code API <<");
        }
        // login user name must exists
        if (forgotPasswordDto.getUsername() != null) {
            // generate verification code and email
            userService.generateAndSendVerificationToken(forgotPasswordDto.getUsername(), request);

            if (log.isInfoEnabled()) {
                log.info("Resending verification code for user id [{}]", forgotPasswordDto.getUsername());
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(),
                            UserConstants.MSG_VERIFICATION_CODE_RESEND_SUCCESSFUL),
                    HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            if (log.isErrorEnabled()) {
                log.error("No authenticated user");
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_NO_AUTH_ERROR),
                    HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * PUT method to validate verification code
     * 
     * @param code
     * @param userInfo
     * @param response
     * @return
     */
    @ApiOperation(value = "Confirm Email using the verification code send")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "Verification code verified successfully"),
                    @ApiResponse(code = 400, message = "Invalid or expired verification code")})
    @PutMapping(value = "/account/confirmEmail")
    public ResponseEntity<GenericResponse> validateNewUserRegistration(
    		@RequestBody @Valid final UserVerificationCodeDto userVerificationCodeDto, 
    		final HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Confirm Email Action API <<");
        }
        // login email id and verification code must exists 
        if (userVerificationCodeDto.getVerificationCode() != null && userVerificationCodeDto.getEmailId()!= null) {
            if (log.isInfoEnabled()) {
                log.info("Validating verification code  [{}] and email id [{}]", userVerificationCodeDto.getVerificationCode(), 
                		userVerificationCodeDto.getEmailId());
            }
            // verify
            if (userService.isValidVerificationCode(userVerificationCodeDto.getVerificationCode(), 
            		userVerificationCodeDto.getEmailId(), request)) {

                return new ResponseEntity<>(new GenericResponse(
                        getMessage(Locale.getDefault(),
                                UserConstants.MSG_VERIFICATION_CODE_VERIFY_SUCCESSFUL),
                        HttpStatus.OK.value()), HttpStatus.OK);
            } else {
                if (log.isErrorEnabled()) {
                    log.error("Invalid verification code [{}] by user id [{}]",
                    		userVerificationCodeDto.getVerificationCode(), userVerificationCodeDto.getEmailId());
                }
                return new ResponseEntity<>(new GenericResponse(
                        getMessage(Locale.getDefault(),
                                UserConstants.MSG_INVALID_VERIFICATION_CODE),
                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("No authenticated user");
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_NO_AUTH_ERROR),
                    HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
        }
    }    
    /**
     * POST method to refresh Access Token
     * 
     * @param userInfo
     * @return
     */
    @ApiOperation(value = "Verification code resend API")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "Access token resent successfully"),
                    @ApiResponse(code = 401, message = "Invalid or expired Refresh token")})
    @PostMapping(value = "/auth/refresh")
    public ResponseEntity<GenericResponse> refeshAccessToken(
    		@RequestHeader(UserConstants.JWT_HEADER) String refreshToken,
    		final HttpServletResponse response) {
        if (log.isInfoEnabled()) {
            log.info("Refresh Access Token <<");
        }
            // validated and generate new access token  
            if (userService.isValidRefreshToken(refreshToken,response)) {
                if (log.isInfoEnabled()) {
                    log.info("Refresh Token is generated ");
                }
                return new ResponseEntity<>(new GenericResponse(
                        getMessage(Locale.getDefault(),
                                UserConstants.MSG_VERIFICATION_CODE_RESEND_SUCCESSFUL),
                        HttpStatus.OK.value()),HttpStatus.OK);
            } else {
                if (log.isErrorEnabled()) {
                    log.error("Refresh Token had expired");
                }
                return new ResponseEntity<>(new GenericResponse(
                        getMessage(Locale.getDefault(), UserConstants.MSG_NO_AUTH_ERROR),
                        HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
            }
    }
    
        /**
     * Get API for getting the user details
     * 
     * @param orgId
     * @return
     */
    @ApiOperation(value = "Get User Details API")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User object"),
            @ApiResponse(code = 404, message = "User not found")})
    @GetMapping(value = "/account/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(
            @PathVariable(UserConstants.USER_ID) final Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Getting the user details user [{}]", userId);
        }
        // get user
        Optional<UserDto> userDtoOpt = userService.getUser(userId);

        if (userDtoOpt.isPresent()) {
            return new ResponseEntity<>(userDtoOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Post API for creating user
     * 
     * @param orgId
     * @param clientId
     * @param userDto
     * @param userInfo
     * @param request
     * @return
     */
    @ApiOperation(value = "Register User API")
    @ApiResponses(
            value = {@ApiResponse(code = 201, message = "User created successfully"), @ApiResponse(
                    code = 400, message = "Invalid or missing input data in the request body")})
    @PostMapping(value = "/account/addUser")
    public ResponseEntity<GenericResponse> registerUser(
            @RequestBody @Valid final UserRegistrationDto userRegDto, @WithUser UserInfo userInfo,
            final HttpServletRequest request) {
        // create user
        Optional<JPAUser> jpaUserOpt = userService.registerNewUser(userRegDto);

        if (jpaUserOpt.isPresent()) {
            JPAUser jpaUser = jpaUserOpt.get();
            if (log.isInfoEnabled()) {
                log.info("User created successfully. User id [{}]", jpaUser.getUserId());
            }
            // for generating and ending verification code email
            userService.generateAndSendVerificationToken(jpaUser.getLogonId(), request);

            return new ResponseEntity<>(new GenericResponse(getMessage(Locale.getDefault(),
                    UserConstants.MSG_USER_CREATE_SUCCESSFUL, userRegDto.getEmail()),
                    HttpStatus.CREATED.value()), HttpStatus.CREATED);

        } else {
            if (log.isErrorEnabled()) {
                log.error("Failed to create user");
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_USER_APP_ERROR),
                    UserConstants.USER_CREATE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Post API for setting user credentials
     * <li>If given token is valid, API will update user's username and password</li>
     * 
     * @param resetPasswordDto
     * @return
     */
    @ApiOperation(value = "Set User Credential API")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "User credentials update is successful"),
                    @ApiResponse(code = 400,
                            message = "Invalid or missing input data in the request body")})
    @PutMapping(value = "/account/credential")
    public ResponseEntity<GenericResponse> setUserCredential(
            @RequestBody @Valid final UserCredentialDto userCredentialDto,
            final HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Set user credential API call");
        }
        Optional<Long> userIdOpt =
                userService.setUserCredential(userCredentialDto.getToken(), userCredentialDto);

        if (userIdOpt.isPresent()) {
            if (log.isInfoEnabled()) {
                log.info("Setting user credential is successful for user id [{}]", userIdOpt.get());
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_SET_CREDENTIAL_SUCCESSFUL),
                    HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Setting user credential failed");
            }
            return new ResponseEntity<>(
                    new GenericResponse(
                            getMessage(Locale.getDefault(),
                                    UserConstants.MSG_SET_CREDENTIAL_FAILED),
                            UserConstants.SET_CREDENTIAL_ERROR, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Patch API to update user details
     * 
     * @param userId
     * @param userUpdateDto
     * @param userInfo
     * @param request
     * @return
     */
    @ApiOperation(value = "Update User API")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "User updated successfully"),
            @ApiResponse(code = 400, message = "Invalid or missing input data in the request body"),
            @ApiResponse(code = 404, message = "User not found")})
    @PatchMapping(value = "/account/{userId}")
    public ResponseEntity<GenericResponse> updateUser(	
            @PathVariable(UserConstants.USER_ID) final Long userId,
            @RequestBody @Valid final UpdateUserDto userUpdateDto,
            @WithUser UserInfo userInfo, final HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Updating user [{}] by user [{}]", userId, 
                    userInfo.getUserId());
        }
        // login user equal to user id who requested the update
        boolean userProfileUpdate = userId.compareTo(userInfo.getUserId()) == 0;

        // update user
        Optional<JPAUser> jpaUserOpt =
                userService.updateUser(userId, userUpdateDto, userProfileUpdate);

        if (jpaUserOpt.isPresent()) {
            JPAUser jpaUser = jpaUserOpt.get();
            if (log.isInfoEnabled()) {
                log.info("User updated successfully. User id [{}]", jpaUser.getUserId());
            }

            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_USER_UPDATE_SUCCESSFUL),
                    HttpStatus.OK.value()), HttpStatus.OK);

        } else {
            if (log.isErrorEnabled()) {
                log.error("Failed to update user");
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_USER_APP_ERROR),
                    UserConstants.USER_UPDATE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Delete API to disable user
     * 
     * @param userId
     * @param userInfo
     * @return
     */
    @ApiOperation(value = "Delete User API")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "User delete not allowed")})
    @DeleteMapping(value = "/account/{userId}")        
    public ResponseEntity<GenericResponse> deleteUser(
            @PathVariable(UserConstants.USER_ID) final Long userId, @WithUser UserInfo userInfo) {
        if (log.isInfoEnabled()) {
            log.info("Deleting user [{}] under by user [{}]", userId, 
                    userInfo.getUserId());
        }
        if (userId.compareTo(userInfo.getUserId()) == 0) {
            return new ResponseEntity<>(
                    new GenericResponse(
                            getMessage(Locale.getDefault(),
                                    UserConstants.MSG_USER_DELETE_NOT_ALLOWED),
                            UserConstants.USER_DELETE_ERROR, HttpStatus.FORBIDDEN.value()),
                    HttpStatus.FORBIDDEN);
        }
        // delete user
        userService.deleteUser(userId, userInfo.getRole());

        return new ResponseEntity<>(new GenericResponse(
                getMessage(Locale.getDefault(), UserConstants.MSG_USER_UPDATE_SUCCESSFUL),
                HttpStatus.OK.value()), HttpStatus.OK);
    }

    /**
     * Put API for change password
     * 
     * @param clientId
     * @param userId
     * @param changePasswordDto
     * @param userInfousers
     * @return
     */
    @ApiOperation(value = "Change Password API")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Change password successful"),
            @ApiResponse(code = 400, message = "Invalid or missing input data in the request body"),
            @ApiResponse(code = 404, message = "User not found")})
    @PutMapping(value = "/account/{userId}/change-password")
    public ResponseEntity<GenericResponse> changePassword(
            @PathVariable(UserConstants.USER_ID) final Long userId,
            @RequestBody @Valid final ChangePasswordDto changePasswordDto,
            @WithUser UserInfo userInfo) {
        if (log.isInfoEnabled()) {
            log.info("Change password API call for user id [{}]", userId);
        }
        // login user id must exists and should be equal to user id of change password request
        if (userInfo.getUserId() != null && (userId.compareTo(userInfo.getUserId()) == 0)) {
            // change password
            userService.changePassword(userId, changePasswordDto);

            if (log.isInfoEnabled()) {
                log.info("Change password is successful for user id [{}]", userId);
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_CHANGE_PASSWORD_SUCCESSFUL),
                    HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            if (log.isErrorEnabled()) {
                log.error("No authorization to change password");
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_NO_AUTH_ERROR),
                    HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get API for getting all the users for a given client
     * <li>if query param active = true, then API will return only active users</li>
     * 
     * @param clientId
     * @param active
     * @return
     */
    @ApiOperation(value = "Get all active/inactive Users")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "List of users"),
            @ApiResponse(code = 204, message = "No users")})
    @GetMapping(value = "/account/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(value = UserConstants.PARAM_ACTIVE,
                    defaultValue = UserConstants.TRUE) boolean active,
            @RequestParam(value = UserConstants.PAGE_NO, 
    		defaultValue = UserConstants.DEFAULT_PAGE_NO, required = false) Integer pageNo, 
		    @RequestParam(value = UserConstants.PAGE_SIZE, 
		    		defaultValue = UserConstants.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
		    @RequestParam(defaultValue = UserConstants.USER_ID, required = false) String sortBy) {
        if (log.isInfoEnabled()) {
            log.info("Pagination Params Passed pageNo {} or pageSize {} or sortBy {} ",
                    pageNo, pageSize, sortBy);                  
        }
        //Add Pagination/Sort details 
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        // get users
        Page<UserDto> userList = userService.getUsers(active, paging);

        if (userList == null || userList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    /**
     * Post API for forgot password
     * <li>If given username (logonid) is valid and exists, API will trigger reset password
     * email</li>
     * 
     * @param forgotPasswordDto
     * @return
     */
    @ApiOperation(value = "Forgot Password API")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Forgot password is successful"),
            @ApiResponse(code = 400, message = "Invalid or missing input data in the request body"),
            @ApiResponse(code = 404, message = "User not found")})
    @PostMapping(value = "/account/forgot-password")
    public ResponseEntity<GenericResponse> forgotPassword(
            @RequestBody @Valid final ForgotPasswordDto forgotPasswordDto,
            final HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("Forgot password API call for username [{}]", forgotPasswordDto.getUsername());
        }
        // check if user exists for given username
        Optional<JPAUser> jpaUserOpt =
                userService.findByLogonId(forgotPasswordDto.getUsername(), true);

        if (jpaUserOpt.isPresent()) {
            JPAUser jpaUser = jpaUserOpt.get();
            // create password reset token and send email
            Optional<JPAPasswordResetToken> passwordToken = userService
                    .createPasswordResetTokenForUser(jpaUser, UUID.randomUUID().toString());

            if (passwordToken.isPresent()) {
                if (log.isInfoEnabled()) {
                    log.info("Sending reset password email to User id [{}]", jpaUser.getUserId());
                }

                // async call for sending email
                userService.sendResetPasswordEmailForUser(jpaUser, passwordToken.get(), request);
            }

            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_FORGOT_PASSWORD_SUCCESSFUL),
                    HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            if (log.isErrorEnabled()) {
                log.error("User [{}] not found", forgotPasswordDto.getUsername());
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_USER_NOT_FOUND),
                    HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Post API for password reset
     * <li>If given token is valid, API will update user's password</li>
     * 
     * @param resetPasswordDto
     * @return
     */
    @ApiOperation(value = "Reset Password API")
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "Password reset successful"), @ApiResponse(
                    code = 400, message = "Invalid or missing input data in the request body")})
    @PutMapping(value = "/account/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(
            @RequestBody @Valid final ResetPasswordDto resetPasswordDto) {
        if (log.isInfoEnabled()) {
            log.info("Reset password API call with token");
        }
        // reset password
        Optional<Long> userIdOpt =
                userService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto);

        if (userIdOpt.isPresent()) {
            if (log.isInfoEnabled()) {
                log.info("Setting user password is successful for user id [{}]", userIdOpt.get());
            }
            return new ResponseEntity<>(new GenericResponse(
                    getMessage(Locale.getDefault(), UserConstants.MSG_RESET_PASSWORD_SUCCESSFUL),
                    HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Password reset failed for token [{}]", resetPasswordDto.getToken());
            }
            return new ResponseEntity<>(
                    new GenericResponse(
                            getMessage(Locale.getDefault(),
                                    UserConstants.MSG_RESET_PASSWORD_FAILED),
                            UserConstants.RESET_PASSWORD_ERROR, HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method to get message from properties file using message source
     * 
     * @param locale
     * @param messageKey
     * @param messageParams
     * @return
     */
    private String getMessage(Locale locale, String messageKey, String... messageParams) {
        return messageSource.getMessage(messageKey, messageParams, locale);
    }
}
