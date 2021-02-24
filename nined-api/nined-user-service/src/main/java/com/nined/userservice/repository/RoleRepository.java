package com.nined.userservice.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nined.userservice.model.JPAStore;
import com.nined.userservice.model.JPARole;
import com.nined.userservice.model.JPAUser;

/**
 * Repository class for ROLE table
 * 
 * @author vijay
 *
 */
@Repository
public interface RoleRepository extends JpaRepository<JPARole, Serializable> {

    @Query("SELECT t FROM JPARole t where t.name = ?1")
    public Optional<JPARole> findBy(String name);

    @Query("SELECT t FROM JPARole t where (t.store = ?1 or t.store is null)")
    public List<JPARole> findBy(JPAStore store);

    @Query("SELECT t FROM JPARole t where t.roleId = ?1")
    public Optional<JPARole> findById(Long roleId);
    
    @Query("SELECT t FROM JPARole t where t.name in (?1)")
    public List<JPARole> findBy(List<String> name);

    @Query("SELECT t FROM JPARole t, JPAUserRole u where t = u.role "
    		+ "AND u.user = ?1 AND (t.store = ?2 or t.store is null)")
	public Optional<JPARole> findByUser(JPAUser user, JPAStore client);
}
