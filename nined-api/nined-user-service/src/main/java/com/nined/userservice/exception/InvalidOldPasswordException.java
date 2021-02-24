package com.nined.userservice.exception;

/**
 * Exception when given password does not match with password in the database for the user
 * 
 * @author vijay
 *
 */
public final class InvalidOldPasswordException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -3277447694940440635L;

    public InvalidOldPasswordException() {
        super();
    }

    public InvalidOldPasswordException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidOldPasswordException(final String message) {
        super(message);
    }

    public InvalidOldPasswordException(final Throwable cause) {
        super(cause);
    }

}
