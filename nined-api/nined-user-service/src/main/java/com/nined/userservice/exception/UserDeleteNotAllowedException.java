package com.nined.userservice.exception;

/**
 * Exception when user delete is not allowed
 * 
 * @author vijay
 *
 */
public class UserDeleteNotAllowedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -1002001579967312156L;

    public UserDeleteNotAllowedException() {
        super();
    }

    public UserDeleteNotAllowedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserDeleteNotAllowedException(final String message) {
        super(message);
    }

    public UserDeleteNotAllowedException(final Throwable cause) {
        super(cause);
    }
}
