package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAFeature;

/**
 * Repository class for FEATURE table
 * 
 * @author vijay
 *
 */
@Repository
public interface FeatureRepository extends JpaRepository<JPAFeature, Serializable> {

    public Optional<JPAFeature> findByName(String name);

    public List<JPAFeature> findByDefaultFeature(boolean defaultFeature);
}
