package com.contaazul.bankslip.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.time.LocalDateTime;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.contaazul.bankslip.exception.ResourceAlreadyExists;
import com.contaazul.bankslip.exception.ResourceNotFoundException;
import com.contaazul.bankslip.exception.model.MessageError;
import com.contaazul.bankslip.exception.model.ResponseError;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {
	
	private static final String ERROR = "error: ";


	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public HttpEntity<ResponseError> handlerHttpRequestMethodNotSupportedException(Exception ex) {
		log.debug(ERROR + ex);

		ResponseError error = ResponseError.builder()
								.status(METHOD_NOT_ALLOWED.value())
								.error(METHOD_NOT_ALLOWED.name())
								.msg(ex.getMessage())
								.timestamp(LocalDateTime.now())
								.build();

		return new ResponseEntity<>(error, METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public HttpEntity<ResponseError> handlerResourceNotFoundException(final ResourceNotFoundException ex) {
		log.debug(ERROR + ex);

		ResponseError error = ResponseError.builder()
								.status(NOT_FOUND.value())
								.error(NOT_FOUND.name())
								.msg(MessageError.RESOURCE_NOT_FOUND.getMsg())
								.timestamp(LocalDateTime.now())
								.build();

		return new ResponseEntity<>(error, NOT_FOUND);
	}


	@ExceptionHandler(ResourceAlreadyExists.class)
	public HttpEntity<ResponseError> handlerResourceNotFoundException(final ResourceAlreadyExists ex) {
		log.debug(ERROR + ex);

		ResponseError error = ResponseError.builder()
								.status(CONFLICT.value())
								.error(CONFLICT.name())
								.msg(ex.getMessage())
								.timestamp(LocalDateTime.now())
								.build();

		return new ResponseEntity<>(error, CONFLICT);
	}
	
	

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public HttpEntity<ResponseError> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.debug(ERROR + ex);

		ResponseError error = ResponseError.builder()
								.status(UNPROCESSABLE_ENTITY.value())
								.error(UNPROCESSABLE_ENTITY.name())
								.msg(MessageError.UNPROCESSABLE_ENTITY.getMsg())
								.timestamp(LocalDateTime.now())
								.build();

		return new ResponseEntity<>(error, UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public HttpEntity<ResponseError> handleMethodNotSupportedExceptions(HttpMessageNotReadableException ex) {
		log.debug(ERROR + ex);

		ResponseError error = ResponseError.builder()
								.status(BAD_REQUEST.value())
								.error(BAD_REQUEST.name())
								.msg("Bankslip not provided in the request body")
								.timestamp(LocalDateTime.now())
								.build();

		return new ResponseEntity<>(error, BAD_REQUEST);
	}

//	@ExceptionHandler(Exception.class)
//	public final HttpEntity<ResponseError> handleAllExceptions(Exception ex) {
//		log.debug(ERROR + ex);
//		ResponseError error = ResponseError.builder()
//								.status(INTERNAL_SERVER_ERROR.value())
//								.error(INTERNAL_SERVER_ERROR.name())
//								.msg("Ocorreu um problema interno.")
//								.timestamp(LocalDateTime.now())
//								.build();
//
//		return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
//	}
}
