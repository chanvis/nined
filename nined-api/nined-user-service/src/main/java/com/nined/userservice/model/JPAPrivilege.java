package com.nined.userservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nined.userservice.enums.Privilege;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class for PRIVILEGE table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "PRIVILEGE")
public class JPAPrivilege {

    @Id
    @Column(name = "PRIVILEGE_ID")
    private Long privilegeId;

    @Column(name = "NAME", nullable = false)
    @Enumerated(EnumType.STRING)
    private Privilege privilege;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    @Column(name = "LASTUPDATED_DATE", nullable = false)
    private Long lastUpdatedDate;
}
