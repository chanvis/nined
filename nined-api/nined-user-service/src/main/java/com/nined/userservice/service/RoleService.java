package com.nined.userservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nined.userservice.constants.UserConstants;
import com.nined.userservice.dto.RoleDto;
import com.nined.userservice.exception.StoreNotFoundException;
import com.nined.userservice.exception.UserNotFoundException;
import com.nined.userservice.model.JPAStore;
import com.nined.userservice.model.JPARole;
import com.nined.userservice.model.JPAUser;
import com.nined.userservice.repository.RoleRepository;
import com.nined.userservice.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for providing all the required methods to interact with the Role entity
 * 
 * @author vijay
 *
 */
@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;  

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method to find role entity for given role id
     * 
     * @param roleId
     * @return
     */
    public Optional<JPARole> findByRoleId(Long roleId) {
        if (roleId == null) {
            return Optional.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("Finding role entity for a given role id [{}]", roleId);
        }
        return roleRepository.findById(roleId);
    }

    /**Gets respective roles supported by curent role
     * @param role
     * @return
     */
    private List<String> getApplicableRoles(String role) {
    	List<String> applicableRoles = new ArrayList();
        if(role != null && role.equalsIgnoreCase(UserConstants.ROLE_SUPER_ADMIN)) {
        	log.info("Getting Roles for Provider Admin");
        	applicableRoles.add(UserConstants.ROLE_CARRIER);

        } else if (role != null 
        		&& role.equalsIgnoreCase(UserConstants.ROLE_CARRIER)) {
        	log.info("Getting Roles for Client Admin");
        	applicableRoles.add(UserConstants.ROLE_CARRIER);
        	applicableRoles.add(UserConstants.ROLE_VENDOR);
        	applicableRoles.add(UserConstants.ROLE_ADMIN);
        	applicableRoles.add(UserConstants.ROLE_USER);
      	
        } else if (role != null && role.equalsIgnoreCase(UserConstants.ROLE_VENDOR)) {
        	log.info("Getting Roles for Admin");
        	applicableRoles.add(UserConstants.ROLE_VENDOR);
        	applicableRoles.add(UserConstants.ROLE_ADMIN);
        	applicableRoles.add(UserConstants.ROLE_USER);
        }
        return applicableRoles;
	}

	/**
     * Method to get role details
     * 
     * @param roleId
     * @return
     */
    public Optional<RoleDto> getRole(Long roleId) {
        if (roleId == null) {
            return Optional.empty();
        }
        Optional<JPARole> jpaRoleOpt = findByRoleId(roleId);
        if (!jpaRoleOpt.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(convertToDTO(jpaRoleOpt.get()));
    }


	
    /**
     * Method to convert entity object to DTO object
     * 
     * @param jpaRole
     * @return
     */
    private RoleDto convertToDTO(JPARole jpaRole) {
        return modelMapper.map(jpaRole, RoleDto.class);
    }



}
