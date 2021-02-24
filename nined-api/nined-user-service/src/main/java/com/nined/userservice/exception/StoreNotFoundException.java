package com.nined.userservice.exception;

/**
 * Exception when given client not found
 * 
 * @author vijay
 *
 */
public class StoreNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7756446969469425082L;

    public StoreNotFoundException() {
        super();
    }

    public StoreNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StoreNotFoundException(final String message) {
        super(message);
    }

    public StoreNotFoundException(final Throwable cause) {
        super(cause);
    }
}
