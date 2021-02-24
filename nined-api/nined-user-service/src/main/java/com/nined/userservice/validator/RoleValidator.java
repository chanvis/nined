package com.nined.userservice.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.nined.userservice.repository.RoleRepository;

/**
 * Custom constraint validator for validating if if given role id exists
 * 
 * @author vijay
 */
public class RoleValidator implements ConstraintValidator<ValidRole, Long> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean isValid(final Long roleId, final ConstraintValidatorContext context) {

        if (roleId == null) {
            // handled by not null constraint
            return true;
        }

        return roleRepository.findById(roleId).isPresent();
    }
}
