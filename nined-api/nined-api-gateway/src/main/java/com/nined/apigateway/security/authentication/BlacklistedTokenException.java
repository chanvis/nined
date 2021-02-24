package com.nined.apigateway.security.authentication;

/**
 * Exception thrown when JWT token presented is blacklisted
 * 
 * @author vijay
 *
 */
public final class BlacklistedTokenException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3741264207547334393L;

    public BlacklistedTokenException() {
        super();
    }

    public BlacklistedTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BlacklistedTokenException(final String message) {
        super(message);
    }

    public BlacklistedTokenException(final Throwable cause) {
        super(cause);
    }

}
