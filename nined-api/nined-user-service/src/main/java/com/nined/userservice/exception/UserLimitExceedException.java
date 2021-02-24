package com.nined.userservice.exception;

/**
 * Exception when number of active users exceeds user limit
 * 
 * @author vijay
 *
 */
public class UserLimitExceedException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7079761835442609359L;

    public UserLimitExceedException() {
        super();
    }

    public UserLimitExceedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserLimitExceedException(final String message) {
        super(message);
    }

    public UserLimitExceedException(final Throwable cause) {
        super(cause);
    }
}
