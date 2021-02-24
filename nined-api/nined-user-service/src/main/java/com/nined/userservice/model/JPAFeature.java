package com.nined.userservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class for FEATURE table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "FEATURE")
public class JPAFeature {

    @Id
    @Column(name = "FEATURE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long featureId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DEFAULT_FEATURE", nullable = false)
    private boolean defaultFeature;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    @Column(name = "LASTUPDATED_DATE", nullable = false)
    private Long lastUpdatedDate;

}
