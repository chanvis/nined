package com.nined.userservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nined.userservice.constants.UserConstants;
import com.nined.userservice.dto.ChangePasswordDto;
import com.nined.userservice.dto.ResetPasswordDto;
import com.nined.userservice.dto.RoleDto;
import com.nined.userservice.dto.UpdateUserDto;
import com.nined.userservice.dto.UserCredentialDto;
import com.nined.userservice.dto.UserDto;
import com.nined.userservice.dto.UserRegistrationDto;
import com.nined.userservice.enums.Privilege;
import com.nined.userservice.enums.Role;
import com.nined.userservice.exception.InvalidOldPasswordException;
import com.nined.userservice.exception.StoreNotFoundException;
import com.nined.userservice.exception.UserAlreadyExistException;
import com.nined.userservice.exception.UserNotFoundException;
import com.nined.userservice.mail.EmailService;
import com.nined.userservice.mail.Mail;
import com.nined.userservice.model.JPALanguage;
import com.nined.userservice.model.JPAPasswordResetToken;
import com.nined.userservice.model.JPAPrivilege;
import com.nined.userservice.model.JPARole;
import com.nined.userservice.model.JPAStore;
import com.nined.userservice.model.JPAUser;
import com.nined.userservice.model.JPAUserRole;
import com.nined.userservice.model.JPAUserVerificationCode;
import com.nined.userservice.repository.PasswordResetTokenRepository;
import com.nined.userservice.repository.RoleRepository;
import com.nined.userservice.repository.UserRepository;
import com.nined.userservice.repository.UserVerificationCodeRepository;
import com.nined.userservice.security.config.JwtConfig;
import com.nined.userservice.security.userdetails.User;
import com.nined.userservice.security.userdetails.UserInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for providing all the required methods to interact with the User entity
 * 
 * @author vijay
 *
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Value("${provider.admin.logonid:padmin}")
    private String padminLogonId;

    @Value("${provider.admin.password:padminpw}")
    private String padminLogonPassword;

    @Value("${default.locale:en_IN}")
    private String defaultLocale;

    @Value("${provider.admin.email:provider.admin@nined.ca}")
    private String padminEmail;

    @Value("${user.verification.code.expiry:15}")
    private int verificationCodeExpiry;

    @Value("${user.reset.password.token.expiry:1440}")
    private int passwordTokenExpiry;

    @Value("${ui.app.url.set-user-cred}")
    private String uiAppUrlForSetUserCred;

    @Value("${ui.app.url.confirm-email}")
    private String uiAppUrlForConfirmEmail;
    
    @Value("${ui.app.url.reset-password}")
    private String uiAppUrlForResetPassword;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserVerificationCodeRepository userVerificationCodeRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StoreService storeService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * The UserDetails object is used by the authentication manager to load the user from the
     * database
     * <li>load user details from using logon id from USER_ACCOUNT table</li>
     * <li>load user's authorities</li>
     */
    @Override
    public UserDetails loadUserByUsername(final String logonId) {
        final Optional<JPAUser> userOpt = findByLogonId(logonId, true);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("No user found with the logon id: " + logonId);
        }
        JPAUser user = userOpt.get();

        // both user and associated store and associated store should be in active state
        return new User(user.getLogonId(), user.getLogonPassword(),
                user.isActive(),
                getAuthorities(user, false), user.getUserId(), 
                user.getUserRole().getRole().getName(), user.getLanguage().getLocale(), false);
    }

    /**
     * Method to load user's authorities
     * <li>If 2FA is enabled, this will load only pre-auth privilege id, -1</li>
     * 
     * @param role
     * @return
     */
    private Collection<? extends GrantedAuthority> getAuthorities(final JPAUser user,
            boolean preAuthFlow) {
        final List<GrantedAuthority> authorities = new ArrayList<>();

        if (preAuthFlow) {
            authorities.add(new SimpleGrantedAuthority(Privilege.PRE_AUTH.id()));
        } else {
            for (final JPAPrivilege privilege : user.getUserRole().getRole().getPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(privilege.getPrivilegeId().toString()));
            }
        }
        return authorities;
    }

    /**
     * Method to refresh the access token once expired but within refresh token timeline
     * 
     * @param role
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isValidRefreshToken(String refreshToken, HttpServletResponse response) {
    	// 2. validate the header and check the prefix
        if (refreshToken == null || !refreshToken.startsWith(jwtConfig.getPrefix())) {
            return false;
        }

        // 3. Get the token
        String token = refreshToken.replace(jwtConfig.getPrefix(), UserConstants.BLANK_STR);
    	try {
            // 1. Validate the token
        	log.info("Validating JWT refresh token {} in User Serviuce " , token);
            Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes())
                    .parseClaimsJws(token).getBody();
            UserInfo userInfo;
            if (claims != null) {
                String logonId = claims.getSubject();
                final List<String> refreshAuthAuthorities = new ArrayList<>();
                refreshAuthAuthorities.add(Privilege.REFRESH_AUTH.id());
                // 2. Create auth object for authentication
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(logonId, null, refreshAuthAuthorities.stream()
                                .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                // 3. Authenticate the user
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                //4. Find the user if auth is successful
                Optional<JPAUser> jpaUserOpt = findByLogonId(logonId, true);
                if(jpaUserOpt.isPresent()) {
                    // 5. create UserInfo object
                	userInfo = new UserInfo(claims.getSubject(), claims.get(UserConstants.USER_ID, Long.class),
                            claims.get(UserConstants.AUTHORITIES, List.class),
                            claims.get(UserConstants.STORE_ID, Long.class),
                            claims.get(UserConstants.ORG_ID, Long.class),
                            claims.get(UserConstants.ROLE, String.class),
                            claims.get(UserConstants.LANG, String.class));
                	//6. generate the access token and set in response headers 
                   	generateJwt(userInfo, getAuthorities(jpaUserOpt.get(), false), response);
                   	return true;
                } 
            } else {
            	return false;
            }
        } catch (Exception e) {
            // In case of failure.
            SecurityContextHolder.clearContext();
            return false;
        }
        return false;
    }
    /**
     * Method to find User entity for a given user id
     * 
     * @param userId
     * @param active
     * @return
     */
    public Optional<JPAUser> findByUserId(Long userId, boolean active) {
        if (userId == null) {
            return Optional.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("Find user entity for given user id : [{}]", userId);
        }
        Optional<JPAUser> jpaUserOpt = userRepository.findById(userId);

        if (active && jpaUserOpt.isPresent() && !jpaUserOpt.get().isActive()) {
            return Optional.empty();
        }
        return jpaUserOpt;
    }

    /**
     * Method to find User entity for a given logon id
     * 
     * @param logonId - username
     * @param active
     * @return
     */
    public Optional<JPAUser> findByLogonId(String logonId, boolean active) {
        if (StringUtils.isBlank(logonId)) {
            return Optional.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("Find user entity for given logon id : [{}]", logonId);
        }
        Optional<JPAUser> jpaUserOpt = userRepository.findByLogonId(logonId);

        if (active && jpaUserOpt.isPresent() && !jpaUserOpt.get().isActive()) {
            return Optional.empty();
        }
        return jpaUserOpt;
    }

    /**
     * Method to find User entity for a given store and user
     * 
     * @param store
     * @param email
     * @return
     */
    public Optional<JPAUser> findByStoreAndEmail(JPAStore store, String email) {
        if (store == null || StringUtils.isBlank(email)) {
            return Optional.empty();
        }
        return userRepository.findBy(email);
    }

    /**
     * Method to check if user account already exists
     * 
     * @param logonId - username
     * @return
     */
    public boolean accountExists(String logonId, boolean isLogonId) {
        if (StringUtils.isBlank(logonId)) {
            return false;
        }
        if(isLogonId) {
        	return userRepository.findByLogonId(logonId).isPresent();	
        } else {
            return userRepository.findBy(logonId).isPresent();        	
        }
    }

    /**
     * Method to check if user account already exists for a given email under a store for different
     * user
     * 
     * @param userId
     * @param store
     * @param email
     * @return
     */
    public boolean accountExists(Long userId, String email) {
        if (userId == null || StringUtils.isBlank(email)) {
            return false;
        }
        Optional<JPAUser> jpaUserOpt = userRepository.findBy(email);

        return jpaUserOpt.isPresent() && (jpaUserOpt.get().getUserId().compareTo(userId) != 0);
    }

    /**
     * Method to register provider admin account, if not exists. This is the default user account
     * created on server start
     * 
     * 
     * @return true when user is created
     */
    public boolean registerProviderAdminUser() {
        if (accountExists(padminLogonId, true)) {
            return false;
        }
        if (log.isInfoEnabled()) {
            log.info("Creating default provider admin user : [{}]", padminLogonId);
        }
        // get root store entity
        Optional<JPAStore> jpaStoreOpt =
                storeService.findByStoreName(UserConstants.DEFAULT_STORE, true);
        Optional<JPALanguage> jpaLangOpt = languageService.findByLocale(defaultLocale);
        if (!jpaStoreOpt.isPresent() || !jpaLangOpt.isPresent()
                || accountExists(padminEmail, false)) {
        	log.info("jpaStoreOpt : [{}] is not active or does not exist or jpaLangOpt : [{}]", 
        			jpaStoreOpt.isPresent(), jpaLangOpt.isPresent());
            return false;
        }

        long currentDate = Instant.now().getEpochSecond();

        final JPAUser user = new JPAUser();
        user.setLogonId(padminLogonId);
        user.setActive(true);
        user.setLanguage(jpaLangOpt.get());
        user.setFirstName(UserConstants.DEFAULT_PROVIDER_ADMIN_NAME);
        user.setLastName(UserConstants.DEFAULT_PROVIDER_ADMIN_NAME);
        user.setEmail(padminEmail);
        user.setPhone(UserConstants.DEFAULT_PROVIDER_ADMIN_PHONE);
        user.setLogonPassword(passwordEncoder.encode(padminLogonPassword));
        user.setAcctActivated(true);
        user.setUserRole(createUserRole(user, Role.ROLE_SUPER_ADMIN));
        user.setCreatedDate(currentDate);
        user.setLastUpdatedDate(currentDate);

        return userRepository.save(user) != null;
    }

    /**
     * Method to create User-Role entity
     * 
     * @param user
     * @param role
     * @return
     */
    private JPAUserRole createUserRole(JPAUser user, Role role) {
        JPAUserRole userRole = null;
        Optional<JPARole> roleOpt = roleRepository.findBy(role.name());
        if (roleOpt.isPresent()) {
            userRole = new JPAUserRole();
            userRole.setUser(user);
            userRole.setRole(roleOpt.get());
        }
        return userRole;
    }

    /**
     * Method responsible for
     * <li>verify the reset password token</li>
     * <li>reset password</li>
     * <li>delete the reset password token</li>
     * 
     * @param resetPasswordDto
     * @return
     */
    @Transactional
    public Optional<Long> setUserCredential(String token, UserCredentialDto userCredentialDto) {
        if (StringUtils.isBlank(token) || userCredentialDto == null) {
            return Optional.empty();
        }

        Long userId = null;
        Optional<JPAPasswordResetToken> jpaPasswordResetTokenOpt =
                passwordResetTokenRepository.findByToken(token);

        if (jpaPasswordResetTokenOpt.isPresent()) {
            JPAPasswordResetToken jpaPasswordResetToken = jpaPasswordResetTokenOpt.get();
            if (!jpaPasswordResetToken.isExpired()) {
                userId = jpaPasswordResetToken.getUser().getUserId();
                updateCredential(userCredentialDto.getUsername(), userCredentialDto.getPassword(),
                        userId);
            }
            passwordResetTokenRepository.delete(jpaPasswordResetToken);
        }
        return Optional.ofNullable(userId);
    }

    /**
     * Method responsible to update username and password for a given user
     * 
     * @param username
     * @param password
     * @param userId
     */
    private void updateCredential(String username, String password, Long userId) {
        userRepository.updateCredential(username, passwordEncoder.encode(password),
                Instant.now().getEpochSecond(), userId);
    }
    

    /**
     * Method responsible for
     * <li>verify the reset password token</li>
     * <li>reset password</li>
     * <li>delete the reset password token</li>
     * 
     * @param resetPasswordDto
     * @return
     */
    @Transactional
    public Optional<Long> resetPassword(String token, ResetPasswordDto resetPasswordDto) {
        if (StringUtils.isBlank(token) || resetPasswordDto == null) {
            return Optional.empty();
        }
        Long userId = null;
        Optional<JPAPasswordResetToken> jpaPasswordResetTokenOpt =
                passwordResetTokenRepository.findByToken(token);

        if (jpaPasswordResetTokenOpt.isPresent()) {
            JPAPasswordResetToken jpaPasswordResetToken = jpaPasswordResetTokenOpt.get();
            if (!jpaPasswordResetToken.isExpired()) {
                userId = jpaPasswordResetToken.getUser().getUserId();
                updatePassword(userId, resetPasswordDto.getPassword());
            }
            passwordResetTokenRepository.delete(jpaPasswordResetToken);
        }
        return Optional.ofNullable(userId);
    }
    
    /**
     * Method responsible to update password for a given user
     * 
     * @param password
     * @param userId
     */
    private void updatePassword(Long userId, String password) {
        userRepository.updatePassword(passwordEncoder.encode(password),
                Instant.now().getEpochSecond(), userId);
    }
    

    /**
     * Method responsible for
     * <li>check if user entity is available</li>
     * <li>check if old password is valid</li>
     * <li>change password</li>
     * 
     * @param userId
     * @param storeId
     * @param changePasswordDto
     */
    @Transactional
    public Optional<JPAUser> changePassword(Long userId,
            ChangePasswordDto changePasswordDto) {
        if (userId == null || changePasswordDto == null) {
            return Optional.empty();
        }
        Optional<JPAUser> jpaUserOpt = findByUserId(userId, true);

        if (jpaUserOpt.isPresent()) {
            JPAUser jpaUser = jpaUserOpt.get();
            if (!checkIfValidOldPassword(jpaUser, changePasswordDto.getOldPassword())) {
                throw new InvalidOldPasswordException("Incorrect old password");
            }
            updatePassword(userId, changePasswordDto.getNewPassword());
        } else {
            throw new UserNotFoundException("User not found");
        }
        return jpaUserOpt;
    }
    

    /**
     * Method to check of given password matches with password in the database for the given user
     * 
     * @param jpaUser
     * @param oldPassword
     * @return
     */
    private boolean checkIfValidOldPassword(final JPAUser jpaUser, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, jpaUser.getLogonPassword());
    }


    /**
     * Method to save user verification code entity. Before persisting delete if one already exists.
     * 
     * @param user
     * @param verificationCode
     * @return
     */
    private Optional<JPAUserVerificationCode> saveUserVerificationCode(JPAUser user,
            int verificationCode) {
        Optional<JPAUserVerificationCode> userVerificationCodeOpt =
                userVerificationCodeRepository.findByUser(user);
        if (userVerificationCodeOpt.isPresent()) {
            if (log.isDebugEnabled()) {
                log.debug("Deleting existing verification code");
            }
            userVerificationCodeRepository.delete(userVerificationCodeOpt.get());
        }
        JPAUserVerificationCode userVerificationCode = new JPAUserVerificationCode();
        userVerificationCode.setUser(user);
        userVerificationCode.setVerificationCode(verificationCode);
        userVerificationCode.setCreatedDate(Instant.now().getEpochSecond());
        userVerificationCode.setExpiryDate(verificationCodeExpiry);
        return Optional.of(userVerificationCodeRepository.save(userVerificationCode));
    }

    /**
     * Method to generate 6 digit verification code and send email
     * 
     * @param userId
     */
    @Async
    @Transactional
    public void generateAndSendVerificationToken(String userId, 
            HttpServletRequest request) {
    	final Optional<JPAUser> userOpt = findByLogonId(userId, false);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("No user found with the user id: " + userId);
        }
        JPAUser user = userOpt.get();
        int verificationCode = ThreadLocalRandom.current().nextInt(100000, 999999);
        if (log.isDebugEnabled()) {
            log.debug("Generated verification code [{}] for user [{}]", verificationCode, userId);
        }

        // save verification code
        saveUserVerificationCode(user, verificationCode);

        Mail mail = new Mail();
        // no need to set from email as it will always default to no-reply email
        mail.setTo(user.getEmail());
        mail.setSubject("Welcome to Nine D");
        mail.setTemplate("email/welcome-user");
        Map<String, Object> model = new HashMap<>();
        model.put(UserConstants.EMAIL_USER, user);
        model.put(UserConstants.CONFIRM_USER_URL, getUiAppUrl(request, uiAppUrlForConfirmEmail)
                + "?token=" + verificationCode);
        mail.setModel(model);
        emailService.sendEmail(mail);
        if (log.isInfoEnabled()) {
            log.info("Mail sent for for user [{}] with verification code [{}]",  userId, verificationCode);
        }
    }

    /**
     * Method to check if given verification code is valid for given user id
     * <li>on valid verification code, it generates JWT</li>
     * 
     * @param userId
     * @param verificationCode
     * @return
     */
    public boolean isValidVerificationCode(int verificationCode, String userId,
            HttpServletRequest request) {
        final Optional<JPAUser> userOpt = userRepository.findByLogonId(userId);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException(
                    "No user found with the user id: " + userId);
        }
        Optional<JPAUserVerificationCode> userVerificationCodeOpt =
                userVerificationCodeRepository.findByUser(userOpt.get());

        if (userVerificationCodeOpt.isPresent()
                && (userVerificationCodeOpt.get().getVerificationCode() == verificationCode)
                && (!userVerificationCodeOpt.get().isExpired())) {
            // Activate the User
        	JPAUser user = userOpt.get();
            user.setActive(true);
            user.setLastUpdatedDate(Instant.now().getEpochSecond());
            userRepository.save(user);
            sendWelcomeEmailForUser(user, request);
            return true;
        }
        return false;
    }

    /**
     * Method to generate JWT
     * 
     * @param userInfo
     * @param authorities
     * @param response
     */
    private void generateJwt(UserInfo userInfo, Collection<? extends GrantedAuthority> authorities,
            HttpServletResponse response) {
        Long now = System.currentTimeMillis();

        // construct token
        String token = Jwts.builder().setSubject(userInfo.getLogonId())
                .claim(UserConstants.AUTHORITIES,
                        authorities.stream().map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .claim(UserConstants.USER_ID, userInfo.getUserId())
                .claim(UserConstants.STORE_ID, userInfo.getStoreId())
                .claim(UserConstants.ORG_ID, userInfo.getOrgId())
                .claim(UserConstants.ROLE, userInfo.getRole())
                .claim(UserConstants.LANG, userInfo.getLang()) //
                .setIssuedAt(new java.sql.Date(now))
                .setExpiration(new java.sql.Date(now + jwtConfig.getExpiration() * 1000)) // in
                                                                                          // milliseconds
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes()).compact();

        // Add token to header
        response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
    }

    /**
     * Method to create new user
     * 
     * @param userRegDto
     * @param storeId
     * @return
     */
    public Optional<JPAUser> registerNewUser(final UserRegistrationDto userRegDto) {
        if (userRegDto == null) {
            return Optional.empty();
        }

        Optional<JPALanguage> jpaLangOpt = languageService.findByLocale(defaultLocale);
        // check if given email is already registered earlier
        if (accountExists(userRegDto.getEmail(), false)) {
            if (log.isErrorEnabled()) {
                log.error("Given email is already registered");
            }
            throw new UserAlreadyExistException(
                    "Given email is already registered");
        }
        // Get the time value to be stored in unix format
        final long currentTime = Instant.now().getEpochSecond();

        final JPAUser user = new JPAUser();
        // set logon id
        user.setLogonId(userRegDto.getEmail());
        user.setFirstName(userRegDto.getFirstName());
        user.setLastName(userRegDto.getLastName());
        user.setLanguage(jpaLangOpt.get());
        user.setActive(false);
        user.setAcctActivated(false);
        user.setLogonPassword(passwordEncoder.encode(userRegDto.getPassword()));
        user.setEmail(userRegDto.getEmail());
        user.setPhone(userRegDto.getPhone());
        user.setUserRole(createUserRole(user, Role.ROLE_USER));
        user.setCreatedDate(currentTime);
        user.setLastUpdatedDate(currentTime);

        return Optional.of(userRepository.save(user));
    }

    /**
     * Method to update user
     * 
     * @param userId
     * @param userUpdateDto
     * @param storeId
     * @param userProfileUpdate
     * @return
     */
    public Optional<JPAUser> updateUser(final Long userId, final UpdateUserDto userUpdateDto,
            final Long storeId) {
        if (userUpdateDto == null || storeId == null) {
            return Optional.empty();
        }
        // check if given store exists
        Optional<JPAStore> jpaStoreOpt = storeService.findByStoreId(storeId, true);

        if (!jpaStoreOpt.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("store id [{}] does not exists", jpaStoreOpt);
            }
            throw new StoreNotFoundException("store not found");
        }
        // get user entity
        Optional<JPAUser> jpaUserOpt = findByUserId(userId, false);
        if (!jpaUserOpt.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("User [{}] not found", userId);
            }
            throw new UserNotFoundException("User not found");
        }
        JPAUser user = jpaUserOpt.get();

        // check if given updated email is already registered under this store for different user
        if (accountExists(userId, userUpdateDto.getEmail())) {
            if (log.isErrorEnabled()) {
                log.error("Given email is already registered under this store [{}]", storeId);
            }
            throw new UserAlreadyExistException(
                    "Given email is already registered under this store");
        }

        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setActive(userUpdateDto.isActive());
        user.setEmail(userUpdateDto.getEmail());
        if (userUpdateDto.getPhone() != null) {
            user.setPhone(userUpdateDto.getPhone());
        }
        user.setLastUpdatedDate(Instant.now().getEpochSecond());

        return Optional.of(userRepository.save(user));
    }
    
    /**
     * Method to get all the users for a given Client
     * <li>Natural Sorting by User LastName</li>
     * 
     * @param clientId
     * @param active
     * @return
     */
    public Page<UserDto> getUsers(boolean active, Pageable paging) {
        Page<JPAUser> users = null;
       	users =  userRepository.findBy(active, paging);
        if (users != null && !users.isEmpty()) {
            return mapUserPageIntoDtoPage(users, UserDto.class);
        } else {
        	return Page.empty(paging);
        }        
    }    

    /**
     * Method to create a persist reset password token for given user
     * 
     * @param user
     * @param token
     * @return
     */
    public Optional<JPAPasswordResetToken> createPasswordResetTokenForUser(JPAUser user,
            String token) {
        if (user == null || token == null) {
            return Optional.empty();
        }
        JPAPasswordResetToken passwordResetToken = new JPAPasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(passwordTokenExpiry);
        passwordResetToken.setCreatedDate(Instant.now().getEpochSecond());
        return Optional.of(passwordResetTokenRepository.save(passwordResetToken));
    }

    /**
     * Method to prepare and send user credential email for a given user
     * <li>this method is called as part of user creation</li>
     * 
     * @param request
     * @param user
     * @param passwordToken
     */
    public void sendWelcomeEmailForUser(JPAUser user, 
            HttpServletRequest request) {
        if (request == null || user == null ) {
            return;
        }
        Mail mail = new Mail();
        // no need to set from email as it will always default to no-reply email
        mail.setTo(user.getEmail());
        mail.setSubject("User Validated Successfully. Welcome to Nine D");
        mail.setTemplate("email/welcome-user-confirmed");
        Map<String, Object> model = new HashMap<>();
        model.put(UserConstants.EMAIL_USER, user);
        mail.setModel(model);
        emailService.sendEmail(mail);
        if (log.isInfoEnabled()) {
            log.info("Mail sent for for user [{}] after confirming verification code", user.getEmail());
        }        
        
    }
    
    /**
     * Method to update user
     * 
     * @param userId
     * @param userUpdateDto
     * @param clientId
     * @param userProfileUpdate
     * @return
     */
    public Optional<JPAUser> updateUser(final Long userId, final UpdateUserDto userUpdateDto, boolean userProfileUpdate) {
        if (userUpdateDto == null ) {
            return Optional.empty();
        }
        // get user entity
        Optional<JPAUser> jpaUserOpt = findByUserId(userId, false);
        if (!jpaUserOpt.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("User [{}] not found", userId);
            }
            throw new UserNotFoundException("User not found");
        }
        JPAUser user = jpaUserOpt.get();

        // check if given updated email is already registered for different user
        if (accountExists(userId,  userUpdateDto.getEmail())) {
            if (log.isErrorEnabled()) {
                log.error("Given email is already registered");
            }
            throw new UserAlreadyExistException(
                    "Given email is already registered");
        }

        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setActive(userUpdateDto.isActive());
        user.setEmail(userUpdateDto.getEmail());
        if (userUpdateDto.getPhone() != null) {
            user.setPhone(userUpdateDto.getPhone());
        }
        user.setLastUpdatedDate(Instant.now().getEpochSecond());

        return Optional.of(userRepository.save(user));
    }    

    /**
     * Method to delete given user
     * 
     * @param userId
     * @param clientId
     * @param loginUserRole
     */
    @Transactional
    public void deleteUser(Long userId, String loginUserRole) {
        if (userId == null) {
            return;
        }
        Optional<JPAUser> jpaUserOpt = findByUserId(userId, true);

        // check if given user exists
        if (jpaUserOpt.isPresent()) {
            JPAUser jpaUser = jpaUserOpt.get();
            jpaUser.setActive(false);
            jpaUser.setLastUpdatedDate(Instant.now().getEpochSecond());
        } else {
            if (log.isErrorEnabled()) {
                log.error("User {} not found", userId);
            }
            throw new UserNotFoundException("User not found");
        }
    }
    
    /**
     * Method to prepare and send reset password email to user
     * 
     * @param request
     * @param user
     * @param passwordToken
     */
    public void sendResetPasswordEmailForUser(JPAUser user, JPAPasswordResetToken passwordToken,
            HttpServletRequest request) {
        if (request == null || user == null || passwordToken == null) {
            return;
        }
        Mail mail = new Mail();
        // no need to set from email as it will always default to no-reply email
        mail.setTo(user.getEmail());
        mail.setSubject("Reset Password");
        mail.setTemplate("email/reset-password");

        Map<String, Object> model = new HashMap<>();
        model.put(UserConstants.EMAIL_TOKEN, passwordToken);
        model.put(UserConstants.EMAIL_USER, user);
        model.put(UserConstants.EMAIL_RESET_URL, getUiAppUrl(request, uiAppUrlForResetPassword)
                + "?token=" + passwordToken.getToken());

        mail.setModel(model);
        emailService.sendEmail(mail);
    }

 
    /**
     * Method to construct and return front end app base URL
     * 
     * @param request
     * @return
     */
    private String getUiAppUrl(HttpServletRequest request, String url) {
        if (StringUtils.isNotBlank(url)) {
            return url;
        } else {
            return request.getScheme() + "://" + request.getServerName() + ":"
                    + request.getServerPort() + request.getContextPath();
        }
    }

    /**
     * Method to get user details for a given user id and store
     * 
     * @param userId
     * @param storeId
     * @return
     */
    public Optional<UserDto> getUser(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        // get user entity
        Optional<JPAUser> jpaUserOpt = findByUserId(userId, false);
        if (!jpaUserOpt.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("User [{}] not found", userId);
            }
            throw new UserNotFoundException("User not found");
        }
        return Optional.of(convertToDTO(jpaUserOpt.get()));
    }

    /**
     * Method to convert entity object to DTO object
     * 
     * @param jpaUser
     * @return
     */
    private UserDto convertToDTO(JPAUser jpaUser) {
        UserDto userDto = modelMapper.map(jpaUser, UserDto.class);
        if (jpaUser.getUserRole() == null) {
        	log.info("User role is null {}", jpaUser.getUserRole() );
        }
        if(jpaUser.getUserRole() != null) {
        	userDto.setRole(convertToDTO(jpaUser.getUserRole().getRole()));	
        }
        return userDto;
    }

    /**
     * Method to convert entity object to DTO object
     * 
     * @param jpaRole
     * @return
     */
    private RoleDto convertToDTO(JPARole jpaRole) {
        return modelMapper.map(jpaRole, RoleDto.class);
    }

    /**
     * Model mapper explicit mapping
     */
    @PostConstruct
    public void initModelMapper() {
        modelMapper.addMappings(new PropertyMap<JPAUser, UserDto>() {
            @Override
            protected void configure() {
                skip(destination.getRole());
            }
        });
    }
    
    /**
     * Maps the Page {@code entities} of JPADevice type which have to be mapped as input to {@code dtoClass} Page
     * of mapped object with DeviceDto type.
     *
     * @param DeviceDto - type of objects in result page
     * @param JPADevice - type of entity in <code>entityPage</code>
     * @param entities - page of entities that needs to be mapped
     * @param dtoClass - class of result page element
     * @return page - mapped page with objects of type <code>DeviceDto</code>.
     * @NB <code>DeviceDto</code> must has NoArgsConstructor!
     */
    public Page<UserDto> mapUserPageIntoDtoPage(Page<JPAUser> entities, Class<UserDto> dtoClass) {
    	modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    	return entities.map(this::convertToDTO);
    }    
}
