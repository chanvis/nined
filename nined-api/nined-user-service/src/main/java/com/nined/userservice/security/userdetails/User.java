package com.nined.userservice.security.userdetails;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import lombok.Getter;

/**
 * Spring security User object extension
 * 
 * @author vijay
 *
 */
@Getter
public class User extends org.springframework.security.core.userdetails.User {

    /**
     * 
     */
    private static final long serialVersionUID = -6247410902249431851L;

    private final Long userId;

    private final String role;

    private final String lang;

    private final boolean twoFAEnabled;

    public User(String username, String password, boolean enabled,
            Collection<? extends GrantedAuthority> authorities, Long userId,
            String role, String lang, boolean twoFAEnabled) {
        super(username, password, enabled, true, true, true, authorities);
        this.userId = userId;
        this.role = role;
        this.lang = lang;
        this.twoFAEnabled = twoFAEnabled;
    }
}
