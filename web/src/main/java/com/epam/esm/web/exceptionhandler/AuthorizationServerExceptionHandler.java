package com.epam.esm.web.exceptionhandler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

import javax.management.openmbean.OpenDataException;
import java.util.Locale;
import java.util.Map;

/**
 * Class customizing Authentication/Authorization Exception handling at Authorization Server.
 * Customization is needed because default exception handling exposes Exception message to client/user.
 * NOTE: message translation is not used due to OAuth 2.0 specification
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-5.2">Section 5.2, RFC 6749 (OAuth 2.0)</a>
 * <p>
 * NOTE: This class is responsible for exception handling only at authorization server enpoints.
 * Resource endpoints Exception handling customization is not needed because those exceptions are handled
 * with different handling mechanism which uses {@link OAuth2Error} objects,
 * resulting in exception messages are not exposed.
 */
@Component
@RequiredArgsConstructor
public class AuthorizationServerExceptionHandler extends DefaultWebResponseExceptionTranslator {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServerExceptionHandler.class);
    private final MessageSource messageSource;

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        logger.error("Handled in the AuthorizationServerExceptionHandler", e);
        String errorDescription = null;
        if (e instanceof UnauthorizedUserException) {
            errorDescription = messageSource.getMessage("auth-server.error-message.bad-credentials",
                    null, Locale.US);
        } else if (e instanceof ClientAuthenticationException) {
            errorDescription = messageSource.getMessage("auth-server.error-message.client-authentication",
                    null, Locale.US);
        } else if (e instanceof InvalidScopeException) {
            errorDescription = messageSource.getMessage("auth-server.error-message.invalid-scope",
                    null, Locale.US);
        } else if (e instanceof UnsupportedGrantTypeException) {
            errorDescription = messageSource.getMessage("auth-server.error-message.unsupported-grant-type",
                    null, Locale.US);
        } else if (e instanceof UnsupportedResponseTypeException) {
            errorDescription = messageSource.getMessage("auth-server.error-message.unsupported-response-type",
                    null, Locale.US);
        } else if (e instanceof UserDeniedAuthorizationException) {
            errorDescription = messageSource.getMessage("auth-server.error-message.user-denied-authorization",
                    null, Locale.US);
        } else {
            errorDescription = messageSource.getMessage("auth-server.error-message.common",
                    null, Locale.US);
        }
        if (e instanceof OAuth2Exception oe) {
            e = new OAuth2ExceptionWrapper(errorDescription, oe);
        } else {
            e = new OAuth2Exception(errorDescription, e);
        }
        return super.translate(e);
    }

    /**
     * Exception that wraps original OAuth2Exception, changing only Exception
     * message
     */
    private static class OAuth2ExceptionWrapper extends OAuth2Exception {

        private final OAuth2Exception wrappedException;

        public OAuth2ExceptionWrapper(String errorDescription, OAuth2Exception wrappedException) {
            super(errorDescription, wrappedException.getCause());
            this.wrappedException = wrappedException;
        }

        @Override
        public String getOAuth2ErrorCode() {
            return wrappedException.getOAuth2ErrorCode();
        }

        @Override
        public int getHttpErrorCode() {
            return wrappedException.getHttpErrorCode();
        }

        @Override
        public Map<String, String> getAdditionalInformation() {
            return wrappedException.getAdditionalInformation();
        }
    }
}
