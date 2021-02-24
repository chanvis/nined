package com.nined.userservice.model;

import javax.persistence.CascadeType;
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
 * Entity class for USER_ACCOUNT table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "USER_ACCOUNT")
public class JPAUser {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "LOGONID", nullable = false)
    private String logonId;

    @Column(name = "LOGONPASSWORD", nullable = false)
    private String logonPassword;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LANGUAGE_ID", nullable = false)
    private JPALanguage language;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @Column(name = "MIDDLENAME")
    private String middleName;

    @Column(name = "LASTNAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ACCT_ACTIVE", nullable = false)
    private boolean acctActivated;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    @Column(name = "LASTUPDATED_DATE", nullable = false)
    private Long lastUpdatedDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private JPAUserRole userRole;

}
