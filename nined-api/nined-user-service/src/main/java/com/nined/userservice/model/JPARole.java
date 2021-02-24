package com.nined.userservice.model;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class for ROLE table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "ROLE")
public class JPARole {

    @Id
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private JPAStore store;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    @Column(name = "LASTUPDATED_DATE", nullable = false)
    private Long lastUpdatedDate;

    @OneToMany
    @JoinTable(name = "ROLE_PRIVILEGE",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRIVILEGE_ID",
                    referencedColumnName = "PRIVILEGE_ID"))
    private Collection<JPAPrivilege> privileges;
}
