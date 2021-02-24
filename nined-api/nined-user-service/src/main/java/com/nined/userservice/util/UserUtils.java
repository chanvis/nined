package com.nined.userservice.util;

import org.apache.commons.lang.StringUtils;

import com.nined.userservice.enums.Role;

/**
 * Utility class
 * 
 * @author vijay
 *
 */
public class UserUtils {


    // private constructor
    private UserUtils() {}

    /**
     * Method to check if login user is eligible to create/ update/ delete user with the given role
     * <li>Provider admin can create/ update/ delete only client admin and other provider
     * admins</li>
     * <li>Client admin can create/ update/ delete any user except provider admin</li>
     * <li>Admin can create/ update/ delete only advanced Users and end users</li>
     * 
     * @param loginUserRole
     * @param newUserRole
     * @return
     */
    public static boolean isUserRoleEligibleToCreateOrUpdateOrDeleteUser(String loginUserRole,
            String newUserRole) {
        if (StringUtils.isBlank(loginUserRole) || StringUtils.isBlank(newUserRole)) {
            return false;
        }
        boolean eligible = false;
        try {
            switch (Role.valueOf(loginUserRole)) {
                case ROLE_SUPER_ADMIN:
                    if (newUserRole.equals(Role.ROLE_SUPER_ADMIN.name())
                            || newUserRole.equals(Role.ROLE_CARRIER.name())) {
                        eligible = true;
                    }
                    break;
                case ROLE_CARRIER:
                    if (!newUserRole.equals(Role.ROLE_SUPER_ADMIN.name())) {
                        eligible = true;
                    }
                    break;
                case ROLE_VENDOR:
                    if (newUserRole.equals(Role.ROLE_ADMIN.name())
                            || newUserRole.equals(Role.ROLE_USER.name())) {
                        eligible = true;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // do nothing
        }


        return eligible;
    }

}
