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
 * Entity class for LANGUAGE table
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "LANGUAGE")
public class JPALanguage {

    @Id
    @Column(name = "LANGUAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long langId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Column(name = "LOCALE", nullable = false)
    private String locale;

    @Column(name = "CREATED_DATE", nullable = false)
    private Long createdDate;

    @Column(name = "LASTUPDATED_DATE", nullable = false)
    private Long lastUpdatedDate;

}
