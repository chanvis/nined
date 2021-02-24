package com.nined.userservice.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAUserRole;

/**
 * Repository class for USER_ROLE table
 * 
 * @author vijay
 *
 */
@Repository
public interface UserRoleRepository extends JpaRepository<JPAUserRole, Serializable> {

}
