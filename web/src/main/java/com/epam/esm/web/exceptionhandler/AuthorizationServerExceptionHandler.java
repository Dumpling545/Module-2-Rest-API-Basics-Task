package com.epam.esm.web.exceptionhandler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Class customizing Authentication/Authorization Exception handling at Authorization Server.
 * Customization is needed because default exception handling exposes Exception message to client/user.
 */
@Component
@RequiredArgsConstructor
public class AuthorizationServerExceptionHandler extends DefaultWebResponseExceptionTranslator {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServerExceptionHandler.class);
    private final MessageSource messageSource;

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        logger.error("Handled in the AuthorizationServerExceptionHandler", e);
        ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
        Locale locale = LocaleContextHolder.getLocale();
        OAuth2Exception body = responseEntity.getBody();
        String message = null;
        if (e instanceof BadCredentialsException || e instanceof UnauthorizedUserException) {
            message = messageSource.getMessage("auth-server.error-message.bad-credentials",
                    null, locale);
        } else if (e instanceof ClientAuthenticationException) {
            message = messageSource.getMessage("auth-server.error-message.client-authentication",
                    null, locale);
        } else if (e instanceof InvalidScopeException) {
            message = messageSource.getMessage("auth-server.error-message.invalid-scope",
                    null, locale);
        } else if (e instanceof UnsupportedGrantTypeException) {
            message = messageSource.getMessage("auth-server.error-message.unsupported-grant-type",
                    null, locale);
        } else if (e instanceof UnsupportedResponseTypeException) {
            message = messageSource.getMessage("auth-server.error-message.unsupported-response-type",
                    null, locale);
        } else if (e instanceof UserDeniedAuthorizationException) {
            message = messageSource.getMessage("auth-server.error-message.user-denied-authorization",
                    null, locale);
        } else {
            message = messageSource.getMessage("auth-server.error-message.common",
                    null, locale);
        }
        OAuth2Exception exception = OAuth2Exception.create(body.getOAuth2ErrorCode(), message);
        return new ResponseEntity<OAuth2Exception>(exception, responseEntity.getStatusCode());
    }
}
