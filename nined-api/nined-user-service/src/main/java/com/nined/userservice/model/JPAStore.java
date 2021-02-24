package com.nined.userservice.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class for CLIENT table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "STORE")
public class JPAStore {

    @Id
    @Column(name = "STORE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "ENABLE_2FA", nullable = false)
    private boolean enable2FA;
    
    @Column(name = "SETCCURR", nullable = false)
    private String setCurr;

    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    @Column(name = "LASTUPDATED_DATE", nullable = false)
    private Long lastUpdatedDate;
}
