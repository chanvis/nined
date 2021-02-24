package com.nined.userservice.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.nined.userservice.dto.ChangePasswordDto;
import com.nined.userservice.dto.ResetPasswordDto;
import com.nined.userservice.dto.UpdateUserDto;
import com.nined.userservice.dto.UserCredentialDto;
import com.nined.userservice.dto.UserDto;
import com.nined.userservice.dto.UserRegistrationDto;
import com.nined.userservice.model.JPAPasswordResetToken;
import com.nined.userservice.model.JPAUser;

/**
 * User entity interface
 * 
 * @author vijay
 *
 */
public interface UserService extends UserDetailsService {

    public Optional<JPAUser> findByLogonId(String logonId, boolean active);

    public boolean accountExists(String logonId, boolean isLogonId);

    public boolean registerProviderAdminUser();

    public void generateAndSendVerificationToken(String userId, HttpServletRequest request);

    public boolean isValidVerificationCode(int verificationCode, String emailId, HttpServletRequest request);
    
    public Optional<JPAUser> registerNewUser(UserRegistrationDto userDto);

    public Optional<UserDto> getUser(Long userId);

	public boolean isValidRefreshToken(String refreshToken, HttpServletResponse response);
	
	public Optional<Long> setUserCredential(String token, UserCredentialDto userCredentialDto);

    public Optional<Long> resetPassword(String token, ResetPasswordDto resetPasswordDto);
    
    public Optional<JPAUser> changePassword(Long userId, ChangePasswordDto changePasswordDto);    

    public void sendWelcomeEmailForUser(JPAUser user, HttpServletRequest request);

    public Optional<JPAUser> updateUser(Long userId, UpdateUserDto userDto,
            boolean userProfileUpdate);
    
    public void deleteUser(Long userId, String loginUserRole);   
    
    public Page<UserDto> getUsers(boolean active, Pageable paging); 
    
    public Optional<JPAPasswordResetToken> createPasswordResetTokenForUser(JPAUser user,
            String token);  
    
    public void sendResetPasswordEmailForUser(JPAUser user, JPAPasswordResetToken passwordToken,
            HttpServletRequest request);    
}
