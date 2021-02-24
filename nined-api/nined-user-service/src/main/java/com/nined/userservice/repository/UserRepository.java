package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAUser;

/**
 * Repository class for USER_ACCOUNT table
 * 
 * @author vijay
 *
 */
@Repository
public interface UserRepository extends JpaRepository<JPAUser, Serializable> {

    public Optional<JPAUser> findByLogonId(String logonId);

    @Query("SELECT t FROM JPAUser t where t.email = ?1")
    public Optional<JPAUser> findBy(String email);

    /** Find User By Client id with Pagination Params
     * @param client
     * @param active
     * @param pageable
     * @return
     */
   
    @Query("SELECT t FROM JPAUser t where t.active = ?1")
    public List<JPAUser> findBy(boolean active);
    
    /** Find User By Client id with Pagination Params
     * @param client
     * @param active
     * @param pageable
     * @return
     */
   
    @Query("SELECT t FROM JPAUser t where t.active = ?1")
    public Page<JPAUser> findBy(boolean active, Pageable paging);
    
    /** Update Password method 
     * @param password
     * @param updatedDate
     * @param userId
     */
    @Modifying
    @Query("update JPAUser t set t.logonPassword = :password, t.active = true, t.lastUpdatedDate = :updatedDate where t.userId = :userId")
    void updatePassword(@Param("password") String password, @Param("updatedDate") Long updatedDate,
            @Param("userId") Long userId);

    @Modifying
    @Query("update JPAUser t set t.logonId = :username, t.logonPassword = :password, t.active = true, t.acctActivated = true, t.lastUpdatedDate = :updatedDate where t.userId = :userId")
    void updateCredential(@Param("username") String username, @Param("password") String password,
            @Param("updatedDate") Long updatedDate, @Param("userId") Long userId);    
}
