package com.epam.esm.web.exceptionhandler;

import com.epam.esm.service.exception.InvalidCertificateException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Exception handler for exceptions associated with gift certificates
 */
@Order(HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GiftCertificateExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateExceptionHandler.class);
    private final ExceptionHelper helper;
    private final MessageSource messageSource;
    @Value("${cert.error-info.postfix}")
    private int certPostfix;

    @ExceptionHandler(InvalidCertificateException.class)
    public ResponseEntity<Object> handleException(InvalidCertificateException ex, Locale locale) {
        HttpStatus status;
        String message;
        switch (ex.getReason()) {
            case NOT_FOUND:
                status = HttpStatus.NOT_FOUND;
                message = messageSource.getMessage("cert.error-message.not-found",
                        new Object[]{ex.getCertificateId()}, locale);
                break;
            case INVALID_SORT_BY:
                status = HttpStatus.BAD_REQUEST;
                message = messageSource.getMessage("cert.error-message.invalid-sort", null, locale);
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = messageSource.getMessage("cert.error-message.common", null, locale);
        }
        logger.error("Handled in the InvalidCertificateException handler", ex);
        return helper.handle(status, message, certPostfix);
    }
}
