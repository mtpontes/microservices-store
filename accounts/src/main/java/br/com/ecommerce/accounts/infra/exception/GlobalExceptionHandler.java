package br.com.ecommerce.accounts.infra.exception;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.ecommerce.accounts.api.dto.exception.ResponseError;
import br.com.ecommerce.accounts.api.dto.exception.ResponseErrorWithoutMessage;
import br.com.ecommerce.common.exception.CustomForbiddenException;
import br.com.ecommerce.common.exception.InvalidTokenException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final String ENTITY_NOT_FOUND_EXCEPTION = "User not found";
	private final String HTTP_MESSAGE_NOT_READABLE_EXCEPTION = "Malformed or unexpected json format";

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError404(NoResourceFoundException ex) {
		return ResponseEntity
				.status(NOT_FOUND.value())
				.body(new ResponseErrorWithoutMessage(
						NOT_FOUND.value(),
						NOT_FOUND.getReasonPhrase()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseError> handleError401(EntityNotFoundException ex) {
		return ResponseEntity
				.status(UNAUTHORIZED.value())
				.body(new ResponseError(
						UNAUTHORIZED.value(),
						UNAUTHORIZED.getReasonPhrase(),
						ENTITY_NOT_FOUND_EXCEPTION));
	}

	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<ResponseError> handleError401(InternalAuthenticationServiceException ex) {
		return ResponseEntity
				.status(UNAUTHORIZED.value())
				.body(new ResponseError(
						UNAUTHORIZED.value(),
						UNAUTHORIZED.getReasonPhrase(),
						ENTITY_NOT_FOUND_EXCEPTION));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ResponseError> handleError401(BadCredentialsException ex) {
		return ResponseEntity
				.status(UNAUTHORIZED.value())
				.body(new ResponseError(
						UNAUTHORIZED.value(),
						UNAUTHORIZED.getReasonPhrase(),
						ENTITY_NOT_FOUND_EXCEPTION));
	}

	@ExceptionHandler(FailedCredentialsException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError401(FailedCredentialsException ex) {
		return ResponseEntity
				.status(UNAUTHORIZED.value())
				.body(new ResponseErrorWithoutMessage(
						UNAUTHORIZED.value(),
						UNAUTHORIZED.getReasonPhrase()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseError> handleError400(MethodArgumentNotValidException ex) {
		var fields = ex.getFieldErrors().stream()
				.collect(Collectors.toMap(f -> f.getField().toString(), f -> f.getDefaultMessage()));

		return ResponseEntity
				.badRequest()
				.body(new ResponseError(
						BAD_REQUEST.value(),
						BAD_REQUEST.getReasonPhrase(),
						fields));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError400(HandlerMethodValidationException ex) {
		return ResponseEntity
				.badRequest()
				.body(new ResponseErrorWithoutMessage(
						BAD_REQUEST.value(),
						BAD_REQUEST.getReasonPhrase()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseError> handlerErro400IllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity
				.badRequest()
				.body(new ResponseError(
						BAD_REQUEST.value(),
						BAD_REQUEST.getReasonPhrase(),
						ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseError> handleError400HttpMessageNotReadableException(
			HttpMessageNotReadableException ex) {
		return ResponseEntity
				.badRequest()
				.body(new ResponseError(
						BAD_REQUEST.value(),
						BAD_REQUEST.getReasonPhrase(),
						HTTP_MESSAGE_NOT_READABLE_EXCEPTION));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ResponseError> handlerErro415(HttpMediaTypeNotSupportedException ex) {
		String unsupported = Optional.ofNullable(ex.getContentType())
				.map(media -> media.getType() + "/" + media.getSubtype())
				.orElse("unknown");

		String supported = ex.getSupportedMediaTypes().stream()
				.map(mediaType -> mediaType.getType() + "/" + mediaType.getSubtype())
				.collect(Collectors.joining(", "));

		String message = String.format(
				"Unsupported media type '%s'. Supported media types are: %s",
				unsupported,
				supported);

		return ResponseEntity
				.status(UNSUPPORTED_MEDIA_TYPE.value())
				.body(new ResponseError(
						UNSUPPORTED_MEDIA_TYPE.value(),
						UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
						message));
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<?> handleError401(InvalidTokenException ex) {
		log.debug("INVALID TOKEN EXCEPTION MESSAGE: {}", ex.getMessage());
		return ResponseEntity
				.status(UNAUTHORIZED.value())
				.body(new ResponseError(
						UNAUTHORIZED.value(),
						UNAUTHORIZED.getReasonPhrase(),
						InvalidTokenException.DEFAULT_MESSAGE));
	}

	@ExceptionHandler(CustomForbiddenException.class)
	public ResponseEntity<?> handleError403(CustomForbiddenException ex) {
		return ResponseEntity
				.status(FORBIDDEN.value())
				.body(new ResponseError(
						FORBIDDEN.value(),
						FORBIDDEN.getReasonPhrase(),
						CustomForbiddenException.DEFAULT_MESSAGE));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError500(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity
				.internalServerError()
				.body(new ResponseErrorWithoutMessage(
						INTERNAL_SERVER_ERROR.value(),
						INTERNAL_SERVER_ERROR.getReasonPhrase()));
	}
}