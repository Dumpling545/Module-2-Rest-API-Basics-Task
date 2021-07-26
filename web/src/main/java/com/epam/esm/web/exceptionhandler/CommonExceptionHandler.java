package com.epam.esm.web.exceptionhandler;

import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.web.GiftCertificateController;
import com.epam.esm.web.OrderController;
import com.epam.esm.web.TagController;
import com.epam.esm.web.UserController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

/**
 * Exception handler for common exceptions, will be invoked only if there's
 * no matched exception handler methods in other handler classes
 */
@Order(LOWEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);
	private final ExceptionHelper helper;
	private final MessageSource messageSource;
	@Value("${cert.error-info.postfix}")
	private int certPostfix;
	@Value("${tag.error-info.postfix}")
	private int tagPostfix;
	@Value("${order.error-info.postfix}")
	private int orderPostfix;
	@Value("${user.error-info.postfix}")
	private int userPostfix;
	@Value("${common.error-info.postfix}")
	private int commonPostfix;

    private int resolvePostfix(Class controllerClass) {
        int postfix = commonPostfix;
        if (controllerClass.equals(TagController.class)) {
            postfix = tagPostfix;
        } else if (controllerClass.equals(GiftCertificateController.class)) {
            postfix = certPostfix;
        } else if (controllerClass.equals(OrderController.class)) {
            postfix = orderPostfix;
        } else if (controllerClass.equals(UserController.class)) {
            postfix = userPostfix;
        }
        return postfix;
    }

    private int resolvePostfix(HandlerMethod handlerMethod) {
        Class controllerClass = handlerMethod.getMethod().getDeclaringClass();
        return resolvePostfix(controllerClass);
    }

    private int resolvePostfix(Method method) {
        Class controllerClass = method.getDeclaringClass();
        return resolvePostfix(controllerClass);
    }

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<Object> handleException(ServiceException ex, HandlerMethod handlerMethod, Locale locale) {
		int postfix = resolvePostfix(handlerMethod);
		logger.error("Handled in the ServiceException handler", ex);
		String message = messageSource.getMessage("common.error-message.service", null, locale);
		return helper.handle(HttpStatus.INTERNAL_SERVER_ERROR, message, postfix);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleException(ConstraintViolationException ex, HandlerMethod handlerMethod,
	                                              Locale locale) {
		logger.error("Handled in the ConstraintViolationException handler", ex);
		List<String> messages = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
		return helper.handleUnformatted(HttpStatus.BAD_REQUEST,
		                                messages,
		                                resolvePostfix(handlerMethod));
	}


	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
	                                                                     HttpHeaders headers, HttpStatus status,
	                                                                     WebRequest request) {
		logger.error("Handled in the HttpRequestMethodNotSupportedException handler", ex);
		String message = messageSource.getMessage("common.error-message.http-request-method-not-supported",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
	                                                                 HttpHeaders headers, HttpStatus status,
	                                                                 WebRequest request) {
		logger.error("Handled in the HttpMediaTypeNotSupportedException handler", ex);
		String message = messageSource.getMessage("common.error-message.http-media-type-not-supported",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
	                                                                  HttpHeaders headers, HttpStatus status,
	                                                                  WebRequest request) {
		logger.error("Handled in the HttpMediaTypeNotAcceptableException handler", ex);
		String message = messageSource.getMessage("common.error-message.http-media-type-not-acceptable",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
	                                                           HttpStatus status, WebRequest request) {
		logger.error("Handled in the MissingPathVariableException handler", ex);
		String message = messageSource.getMessage("common.error-message.missing-path-variable",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
	                                                                      HttpHeaders headers, HttpStatus status,
	                                                                      WebRequest request) {
		logger.error("Handled in the MissingServletRequestParameterException handler", ex);
		String message = messageSource.getMessage("common.error-message.missing-servlet-request-parameter",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
	                                                                      HttpHeaders headers, HttpStatus status,
	                                                                      WebRequest request) {
		logger.error("Handled in the ServletRequestBindingException handler", ex);
		String message = messageSource.getMessage("common.error-message.servlet-request-binding",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		logger.error("Handled in the ConversionNotSupportedException handler", ex);
		String message = messageSource.getMessage("common.error-message.conversion-not-supported",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
	                                                    HttpStatus status, WebRequest request) {
		logger.error("Handled in the TypeMismatchException handler", ex);
		String message = messageSource.getMessage("common.error-message.type-mismatch",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		logger.error("Handled in the HttpMessageNotReadableException handler", ex);
		String message = messageSource.getMessage("common.error-message.http-message-not-readable",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		logger.error("Handled in the HttpMessageNotWritableException handler", ex);
		String message = messageSource.getMessage("common.error-message.http-message-not-writable",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        logger.error("Handled in the MethodArgumentNotValidException handler", ex);
        List<String> messages = ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
        return helper.handleUnformatted(status, headers, messages, resolvePostfix(ex.getParameter().getMethod()));
    }

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
	                                                                 HttpHeaders headers, HttpStatus status,
	                                                                 WebRequest request) {
		logger.error("Handled in the MissingServletRequestPartException handler", ex);
		String message = messageSource.getMessage("common.error-message.missing-servlet-request-part",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
	                                                     WebRequest request) {
		logger.error("Handled in the BindException handler", ex);
		String message = messageSource.getMessage("common.error-message.bind",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
	                                                               HttpStatus status, WebRequest request) {
		logger.error("Handled in the NoHandlerFoundException handler", ex);
		String message = messageSource.getMessage("common.error-message.no-handler-found",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

	@Override
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
	                                                                    HttpHeaders headers, HttpStatus status,
	                                                                    WebRequest request) {
		logger.error("Handled in the AsyncRequestTimeoutException handler", ex);
		String message = messageSource.getMessage("common.error-message.async-request-timeout",
		                                          null, request.getLocale());
		return helper.handle(status, headers, message, commonPostfix);
	}

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        logger.error("Handled in the fallback handler", ex);
        String message = messageSource.getMessage("common.error-message.other", null, request.getLocale());
        return helper.handle(HttpStatus.INTERNAL_SERVER_ERROR, message, commonPostfix);
    }
}
