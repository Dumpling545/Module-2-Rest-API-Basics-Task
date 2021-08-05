package com.epam.esm.model.validation;

import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.passay.spring.SpringMessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;


/**
 * Constraint validator for {@link ValidPassword} annotation. Uses Passay library for
 * actual password validation.
 *
 * @see <a href="http://www.passay.org/">Passay library</a>
 */
@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	private final MessageSource messageSource;
	private final PasswordConstraintsHolder constraintsHolder;

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		MessageResolver messageResolver = new SpringMessageResolver(messageSource, LocaleContextHolder.getLocale());
		PasswordValidator validator = new PasswordValidator(messageResolver, Arrays.asList(
				new LengthRule(constraintsHolder.minLength(), constraintsHolder.maxLength()),
				new CharacterRule(EnglishCharacterData.UpperCase, constraintsHolder.minUpperCaseOccurrences()),
				new CharacterRule(EnglishCharacterData.LowerCase, constraintsHolder.minLowerCaseOccurrences()),
				new CharacterRule(EnglishCharacterData.Digit, constraintsHolder.minDigitOccurrences()),
				new CharacterRule(EnglishCharacterData.Special, constraintsHolder.minSpecialCharsOccurrences()),
				new IllegalSequenceRule(EnglishSequenceData.Alphabetical,
				                        constraintsHolder.maxAlphaSequenceLength(), false),
				new IllegalSequenceRule(EnglishSequenceData.Numerical, constraintsHolder.maxDigitSequenceLength(),
				                        false),
				new IllegalSequenceRule(EnglishSequenceData.USQwerty, constraintsHolder.maxKeyboardSequenceLength(),
				                        false),
				new WhitespaceRule()));
		RuleResult result = validator.validate(new PasswordData(password));
		context.disableDefaultConstraintViolation();
		validator.getMessages(result)
				.forEach(m -> context.buildConstraintViolationWithTemplate(m).addConstraintViolation());
		return result.isValid();
	}
}
