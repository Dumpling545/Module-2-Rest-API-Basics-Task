package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.web.TagController;
import com.epam.esm.web.helper.ExceptionHelper;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ResourceBundle;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
	private final String CERT_POSTFIX_KEY = "cert.postfix";
	private final int CERT_POSTFIX;
	private final String TAG_POSTFIX_KEY = "tag.postfix";
	private final int TAG_POSTFIX;
	private final String OTHER_KEY = "other";
	private final String OTHER_MSG;
	private final String SERVICE_KEY = "service";
	private final String SERVICE_MSG;
	private final String JSON_KEY = "json";
	private final String JSON_MSG;
	private ExceptionHelper helper;

	@Autowired
	public CommonExceptionHandler(ExceptionHelper helper) {
		this.helper = helper;
		ResourceBundle rb = helper.getErrorMessagesBundle();
		TAG_POSTFIX = Integer.parseInt(rb.getString(TAG_POSTFIX_KEY));
		CERT_POSTFIX = Integer.parseInt(rb.getString(CERT_POSTFIX_KEY));
		SERVICE_MSG = rb.getString(SERVICE_KEY);
		OTHER_MSG = rb.getString(OTHER_KEY);
		JSON_MSG = rb.getString(JSON_KEY);
	}

	private int resolvePostfix(HandlerMethod handlerMethod) {
		Class controller = handlerMethod.getMethod().getDeclaringClass();
		int postfix = CERT_POSTFIX;
		if (controller.equals(TagController.class)) {
			postfix = TAG_POSTFIX;
		}
		return postfix;
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<Error> handleException(ServiceException ex, HandlerMethod handlerMethod) {
		int postfix = resolvePostfix(handlerMethod);
		return helper.handle(HttpStatus.INTERNAL_SERVER_ERROR, SERVICE_MSG, postfix);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> handleException(Exception ex, HandlerMethod handlerMethod) {
		int postfix = resolvePostfix(handlerMethod);
		return helper.handle(HttpStatus.BAD_REQUEST, OTHER_MSG, postfix);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
	                                                                     HttpHeaders headers, HttpStatus status,
	                                                                     WebRequest request) {
		return helper.noPostfixHandle(status, OTHER_MSG);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
	                                                           HttpStatus status, WebRequest request) {
		return helper.noPostfixHandle(status, OTHER_MSG);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
	                                                                      HttpHeaders headers, HttpStatus status,
	                                                                      WebRequest request) {
		return helper.noPostfixHandle(status, OTHER_MSG);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
	                                                    HttpStatus status, WebRequest request) {
		return helper.noPostfixHandle(status, OTHER_MSG);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		return helper.noPostfixHandle(status, JSON_MSG);
	}
}
