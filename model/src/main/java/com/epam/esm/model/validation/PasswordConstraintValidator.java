package com.epam.esm.model.validation;

import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.passay.spring.SpringMessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;


@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Value("${user.password.min-length}")
    private int minLength;
    @Value("${user.password.max-length}")
    private int maxLength;
    @Value("${user.password.min-upper-case-occurrences}")
    private int minimalUpperCaseOccurrences;
    @Value("${user.password.min-lower-case-occurrences}")
    private int minimalLowerCaseOccurrences;
    @Value("${user.password.min-digit-occurrences}")
    private int minimalDigitOccurrences;
    @Value("${user.password.min-special-chars-occurrences}")
    private int minimalSpecialCharsOccurrences;
    @Value("${user.password.max-alpha-sequence-length}")
    private int maximalAlphabeticalSequenceLength;
    @Value("${user.password.max-digit-sequence-length}")
    private int maximalDigitSequenceLength;
    @Value("${user.password.max-keyboard-sequence-length}")
    private int maximalKeyboardSequenceLength;
    private final MessageSource messageSource;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        MessageResolver messageResolver = new SpringMessageResolver(messageSource, LocaleContextHolder.getLocale());
        PasswordValidator validator = new PasswordValidator(messageResolver, Arrays.asList(
                new LengthRule(minLength, maxLength),
                new CharacterRule(EnglishCharacterData.UpperCase, minimalUpperCaseOccurrences),
                new CharacterRule(EnglishCharacterData.LowerCase, minimalLowerCaseOccurrences),
                new CharacterRule(EnglishCharacterData.Digit, minimalDigitOccurrences),
                new CharacterRule(EnglishCharacterData.Special, minimalSpecialCharsOccurrences),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, maximalAlphabeticalSequenceLength, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, maximalDigitSequenceLength, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, maximalKeyboardSequenceLength, false),
                new WhitespaceRule()));
        RuleResult result = validator.validate(new PasswordData(password));
        context.disableDefaultConstraintViolation();
        validator.getMessages(result).forEach(m -> context.buildConstraintViolationWithTemplate(m).addConstraintViolation());
        return result.isValid();
    }
}
