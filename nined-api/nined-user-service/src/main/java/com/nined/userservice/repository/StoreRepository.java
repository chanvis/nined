package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAStore;

/**
 * Repository class for STORE table
 * 
 * @author vijay
 *
 */
@Repository
public interface StoreRepository extends JpaRepository<JPAStore, Serializable> {

    @Query("SELECT t FROM JPAStore t where t.identifier = ?1")
    public Optional<JPAStore> findBy(String identifier);

    @Query("SELECT t FROM JPAStore t where t.storeId = ?1")
    public Optional<JPAStore> findBy(Long storeId);
}
