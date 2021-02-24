package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAUser;
import com.nined.userservice.model.JPAUserVerificationCode;

/**
 * Repository class for USER_VERIFICATION_CODE table
 * 
 * @author vijay
 *
 */
@Repository
public interface UserVerificationCodeRepository
        extends JpaRepository<JPAUserVerificationCode, Serializable> {

    public Optional<JPAUserVerificationCode> findByUser(JPAUser user);
}
