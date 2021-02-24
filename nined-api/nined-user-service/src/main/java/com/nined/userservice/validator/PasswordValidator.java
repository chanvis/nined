package com.nined.userservice.validator;

import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import com.google.common.base.Joiner;

/**
 * Custom constraint validator to check if given password meets all the requirement
 * <li>length between 6 and 25 characters</li>
 * <li>at least one upper-case character</li>
 * <li>at least one lower-case character</li>
 * <li>at least one-digit character</li>
 * <li>at least one symbol (special character)</li>
 * <li>no whitespace</li>
 * 
 * @author vijay
 *
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        // retrun true, as this will be handled by @NotBlank
        if (StringUtils.isBlank(password)) {
            return true;
        }
        org.passay.PasswordValidator validator = new org.passay.PasswordValidator(Arrays.asList( //
                new LengthRule(6, 25), //
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1), //
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                Joiner.on(",").join(validator.getMessages(result))).addConstraintViolation();
        return false;
    }

}
