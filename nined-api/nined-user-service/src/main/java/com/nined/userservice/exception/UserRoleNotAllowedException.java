package com.nined.userservice.exception;

/**
 * Exception when given user role not allowed
 * 
 * @author vijay
 *
 */
public class UserRoleNotAllowedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5690321550288553737L;

    public UserRoleNotAllowedException() {
        super();
    }

    public UserRoleNotAllowedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserRoleNotAllowedException(final String message) {
        super(message);
    }

    public UserRoleNotAllowedException(final Throwable cause) {
        super(cause);
    }
}
