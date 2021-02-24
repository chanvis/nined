package com.nined.userservice.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class for USER_ROLE table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "USER_ROLE")
public class JPAUserRole {

    @Id
    @Column(name = "USER_ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRoleId;

    @OneToOne(optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private JPAUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private JPARole role;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate = Instant.now().getEpochSecond();

}
