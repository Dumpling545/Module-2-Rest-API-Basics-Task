package com.epam.esm.web.exceptionhandler;

import com.epam.esm.model.dto.Error;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.web.GiftCertificateController;
import com.epam.esm.web.TagController;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
	@Value("${cert.error-info.postfix}")
	private int certPostfix;
	@Value("${tag.error-info.postfix}")
	private int tagPostfix;
	@Value("${common.error-info.postfix}")
	private int commonPostfix;
	@Value("${common.error-message.other}")
	private String otherMsg;
	@Value("${common.error-message.service}")
	private String serviceMsg;
	@Value("${common.error-message.json}")
	private String jsonMsg;

	private ExceptionHelper helper;

	@Autowired
	public CommonExceptionHandler(ExceptionHelper helper) {
		this.helper = helper;
	}

	private int resolvePostfix(HandlerMethod handlerMethod) {
		Class controller = handlerMethod.getMethod().getDeclaringClass();
		int postfix = commonPostfix;
		if (controller.equals(TagController.class)) {
			postfix = tagPostfix;
		} else if (controller.equals(GiftCertificateController.class)) {
			postfix = certPostfix;
		}
		return postfix;
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<Error> handleException(ServiceException ex, HandlerMethod handlerMethod) {
		int postfix = resolvePostfix(handlerMethod);
		return helper.handle(HttpStatus.INTERNAL_SERVER_ERROR, serviceMsg, postfix);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> handleException(Exception ex, HandlerMethod handlerMethod) {
		int postfix = resolvePostfix(handlerMethod);
		return helper.handle(HttpStatus.BAD_REQUEST, otherMsg, postfix);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
	                                                                     HttpHeaders headers, HttpStatus status,
	                                                                     WebRequest request) {
		return helper.noPostfixHandle(status, otherMsg);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
	                                                           HttpStatus status, WebRequest request) {
		return helper.noPostfixHandle(status, otherMsg);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
	                                                                      HttpHeaders headers, HttpStatus status,
	                                                                      WebRequest request) {
		return helper.noPostfixHandle(status, otherMsg);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
	                                                    HttpStatus status, WebRequest request) {
		return helper.noPostfixHandle(status, otherMsg);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
	                                                              HttpHeaders headers, HttpStatus status,
	                                                              WebRequest request) {
		return helper.noPostfixHandle(status, jsonMsg);
	}
}
