package com.nined.userservice.validator;

import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import com.google.common.base.Joiner;

/**
 * Custom constraint validator to check if given username meets all the requirement
 * <li>min 3 and max 15 characters</li>
 * <li>must contain alphabetic or alphanumeric character</li>
 * <li>must start with alphabetic character</li>
 * <li>no whitespace</li>
 * 
 * @author vijay
 *
 */
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private static final String REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public void initialize(ValidUsername constraintAnnotation) {}

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        // return true, as this will be handled by @NotBlank
        if (StringUtils.isBlank(username)) {
            return true;
        }
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(Arrays.asList( //
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(username));
        if (result.isValid() && isAlphaNumeric(username)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                Joiner.on(",").join(validator.getMessages(result))).addConstraintViolation();
        return false;
    }

    private static boolean isAlphaNumeric(String str) {
        return str != null && str.matches(REGEX);
    }
}
