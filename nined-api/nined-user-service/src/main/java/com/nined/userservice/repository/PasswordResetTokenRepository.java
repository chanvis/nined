package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAPasswordResetToken;

/**
 * Repository class for PASSWORD_RESET_TOKEN table
 * 
 * @author vijay
 *
 */
@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<JPAPasswordResetToken, Serializable> {

    public Optional<JPAPasswordResetToken> findByToken(String token);
}
