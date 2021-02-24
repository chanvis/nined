package com.nined.userservice.exception;

/**
 * Exception when given user not found
 * 
 * @author vijay
 *
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -266890865697950184L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(final String message) {
        super(message);
    }

    public UserNotFoundException(final Throwable cause) {
        super(cause);
    }
}
