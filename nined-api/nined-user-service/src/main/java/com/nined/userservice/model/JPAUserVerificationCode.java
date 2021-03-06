package com.nined.userservice.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class for USER_VERIFICATION_CODE table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "USER_VERIFICATION_CODE")
public class JPAUserVerificationCode {

    @Id
    @Column(name = "USER_VERIFICATION_CODE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userVerificationCodeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private JPAUser user;

    @Column(name = "VERIFICATION_CODE", nullable = false)
    private int verificationCode;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "EXPIRY_DATE", nullable = false)
    private Long expiryDate;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Set expiry date in min
     * 
     * @param minutes
     */
    public void setExpiryDate(int minutes) {
        this.expiryDate = Instant.now().plus(minutes, ChronoUnit.MINUTES).getEpochSecond();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(Instant.ofEpochSecond(this.expiryDate)
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
}
