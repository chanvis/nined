package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPALanguage;

/**
 * Repository class for LANGUAGE table
 * 
 * @author vijay
 *
 */
@Repository
public interface LanguageRepository extends JpaRepository<JPALanguage, Serializable> {

    public Optional<JPALanguage> findByLocale(String locale);
}
