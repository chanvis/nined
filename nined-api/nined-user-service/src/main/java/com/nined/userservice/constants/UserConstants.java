package com.nined.userservice.constants;

/**
 * Constants utility class
 * 
 * @author vijay
 *
 */
public class UserConstants {

    private UserConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String HYPHEN = "-";
    public static final String COLON = ":";
    public static final String FALSE = "false";
    public static final String TRUE = "true";

    // api and security constants
    public static final String AUTHORITIES = "authorities";
    public static final String ORG_ID = "orgId";
    public static final String STORE_ID = "storeId";
    public static final String USER_ID = "userId";
    public static final String ROLE = "role";
    public static final String LANG = "lang";
    public static final String TOKEN = "token";
    public static final String FEATURE_ID = "featureId";
    public static final String ROLE_NAME = "roleName";

    public static final String ROOT_ORGANIZATION = "Root Organization";
    public static final String DEFAULT_STORE = "NINED";
    public static final String DEFAULT_PROVIDER_ADMIN_NAME = "admin";
    public static final String MAIN_DEVICE_GROUP_NAME = "Main";
    public static final String DEFAULT_PROVIDER_ADMIN_PHONE = "9999988888";

    // email constants
    public static final String EMAIL_SIGNATURE = "signature";
    public static final String EMAIL_TOKEN = "token";
    public static final String VERIFICATION_CODE = "verificationCode";
    public static final String EMAIL_USER = "user";
    public static final String EMAIL_RESET_URL = "resetUrl";
    public static final String CONFIRM_USER_URL = "confirmEmailUrl";

    // API params
    public static final String PARAM_ACTIVE = "active";

    // error codes
    public static final String ORG_ALREADY_EXISTS = "OrgAlreadyExist";
    public static final String ORG_UPDATE_DELETE_NOT_ALLOWED = "OrgUpdateDeleteNotAllowed";
    public static final String ORG_NOT_FOUND = "OrgNotFound";
    public static final String CLIENT_ALREADY_EXISTS = "ClientAlreadyExist";
    public static final String CLIENT_UPDATE_DELETE_NOT_ALLOWED = "ClientUpdateDeleteNotAllowed";
    public static final String CLIENT_NOT_FOUND = "ClientNotFound";
    public static final String CLIENT_LIMIT_EXCEED = "ClientLimitExceed";
    public static final String DEVICE_LIMIT_EXCEED = "DeviceLimitExceed";
    public static final String CLIENT_USER_LIMIT_EXCEED = "ClientUserLimitExceed";

    public static final String USER_CREATE_ERROR = "UserCreateError";
    public static final String USER_UPDATE_ERROR = "UserUpdateError";
    public static final String USER_DELETE_ERROR = "UserDeleteError";
    public static final String USER_DELETE_NOT_ALLOWED = "UserDeleteNotAllowed";
    public static final String USER_ROLE_NOT_ALLOWED = "UserRoleNotAllowed";
    public static final String NO_AUTH_ERROR = "NoAuthError";
    public static final String RESET_PASSWORD_ERROR = "ResetPasswordError";
    public static final String INVALID_OLD_PASSWORD = "InvalidOldPassword";
    public static final String SET_CREDENTIAL_ERROR = "SetCredentialError";
    public static final String USER_ALREADY_EXISTS = "UserAlreadyExist";
    public static final String ROLE_NOT_FOUND = "RoleNotFound";
    public static final String USER_NOT_FOUND = "UserNotFound";
    public static final String JWT_HEADER = "Authorization";
    public static final String BLANK_STR = "";

    // message source constants
    public static final String MSG_VERIFICATION_CODE_RESEND_SUCCESSFUL =
            "msg.verification.code.resend.successful";
    public static final String MSG_VERIFICATION_CODE_VERIFY_SUCCESSFUL =
            "msg.verification.code.verify.successful";
    public static final String MSG_NO_AUTH_ERROR = "msg.no.auth.error";
    public static final String MSG_INVALID_VERIFICATION_CODE = "msg.invalid.verification.code";
    public static final String MSG_ORG_CREATE_ERROR = "msg.org.createError";
    public static final String MSG_ORG_CREATE_SUCCESSFUL = "msg.org.createSuccessful";
    public static final String MSG_ORG_UPDATE_SUCCESSFUL = "msg.org.updateSuccessful";
    public static final String MSG_ORG_DELETE_SUCCESSFUL = "msg.org.deleteSuccessful";
    public static final String MSG_ORG_NOT_FOUND = "msg.org.notFound";
    public static final String MSG_ORG_UPDATE_DELETE_NOT_ALLOWED = "msg.org.updateDeleteNotAllowed";
    public static final String MSG_ORG_CLIENT_LIMIT_EXCEED = "msg.org.clientLimitExceed";

    public static final String MSG_CLIENT_CREATE_ERROR = "msg.client.createError";
    public static final String MSG_CLIENT_CREATE_SUCCESSFUL = "msg.client.createSuccessful";
    public static final String MSG_CLIENT_UPDATE_SUCCESSFUL = "msg.client.updateSuccessful";
    public static final String MSG_CLIENT_DELETE_SUCCESSFUL = "msg.client.deleteSuccessful";
    public static final String MSG_CLIENT_NOT_FOUND = "msg.client.notFound";
    public static final String MSG_CLIENT_UPDATE_DELETE_NOT_ALLOWED =
            "msg.client.updateDeleteNotAllowed";
    public static final String MSG_CLIENT_USER_LIMIT_EXCEED = "msg.client.userLimitExceed";

    public static final String MSG_USER_APP_ERROR = "msg.user.appError";
    public static final String MSG_USER_CREATE_SUCCESSFUL = "msg.user.createSuccessful";
    public static final String MSG_USER_UPDATE_SUCCESSFUL = "msg.user.updateSuccessful";
    public static final String MSG_USER_DELETE_SUCCESSFUL = "msg.user.deleteSuccessful";
    public static final String MSG_USER_ROLE_NOT_ELIGIBLE = "msg.user.userRoleNotEligible";
    public static final String MSG_USER_NOT_FOUND = "msg.user.notFound";
    public static final String MSG_USER_DELETE_NOT_ALLOWED = "msg.user.deleteNotAllowed";
    public static final String MSG_RESET_PASSWORD_SUCCESSFUL = "msg.reset.password.successful";
    public static final String MSG_RESET_PASSWORD_FAILED = "msg.reset.password.failed";
    public static final String MSG_FORGOT_PASSWORD_SUCCESSFUL = "msg.forgot.password.successful";
    public static final String MSG_CHANGE_PASSWORD_SUCCESSFUL = "msg.change.password.successful";
    public static final String MSG_INVALID_OLD_PASSWORD = "msg.invalid.old.password";
    public static final String MSG_SET_CREDENTIAL_SUCCESSFUL = "msg.set.credential.successful";
    public static final String MSG_SET_CREDENTIAL_FAILED = "msg.set.credential.failed";
    
    public static final String PAGE_NO = "pageNo";
    public static final String PAGE_SIZE = "pageSize";
    public static final String DEFAULT_PAGE_NO = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String MAX_PAGE_SIZE = "2000";
    
    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_CARRIER = "ROLE_CARRIER";
    public static final String ROLE_VENDOR = "ROLE_VENDOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_GUEST = "ROLE_GUEST";
}
