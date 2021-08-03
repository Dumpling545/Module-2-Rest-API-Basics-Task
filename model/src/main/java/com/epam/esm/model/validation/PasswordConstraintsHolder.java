package com.epam.esm.model.validation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Helper record for {@link PasswordConstraintValidator} that holds all constraint values.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "user.password")
public record PasswordConstraintsHolder(int minLength, int maxLength, int minUpperCaseOccurrences,
                                        int minLowerCaseOccurrences, int minDigitOccurrences,
                                        int minSpecialCharsOccurrences, int maxAlphaSequenceLength,
                                        int maxDigitSequenceLength, int maxKeyboardSequenceLength) {
}
