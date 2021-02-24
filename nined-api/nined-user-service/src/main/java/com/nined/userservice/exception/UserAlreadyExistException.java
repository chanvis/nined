package com.nined.userservice.exception;

/**
 * Exception when given username already exists
 * 
 * @author vijay
 *
 */
public class UserAlreadyExistException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -8239023285388802699L;

    public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistException(final String message) {
        super(message);
    }

    public UserAlreadyExistException(final Throwable cause) {
        super(cause);
    }
}
