package br.com.ecommerce.products.infra.exception.handlers;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.ecommerce.common.exception.CustomForbiddenException;
import br.com.ecommerce.common.exception.InvalidTokenException;
import br.com.ecommerce.products.infra.exception.exceptions.CategoryNotFoundException;
import br.com.ecommerce.products.infra.exception.exceptions.DepartmentNotFoundException;
import br.com.ecommerce.products.infra.exception.exceptions.ManufacturerNotFoundException;
import br.com.ecommerce.products.infra.exception.exceptions.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final String HTTP_MESSAGE_NOT_READABLE_EXCEPTION = "Malformed or unexpected json format";

	private final HttpStatus notFound = HttpStatus.NOT_FOUND;
	private final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
	private final HttpStatus forbidden = HttpStatus.FORBIDDEN;
	private final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
	private final HttpStatus unsupportedMediaType = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
	private final HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;


	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError404(NoResourceFoundException ex) {
		return ResponseEntity
			.status(notFound.value())
			.body(new ResponseErrorWithoutMessage(
				notFound.value(),
				notFound.getReasonPhrase()));
	}

	@ExceptionHandler(DepartmentNotFoundException.class)
	public ResponseEntity<ResponseError> handleError404(DepartmentNotFoundException ex) {
		return ResponseEntity
			.status(notFound.value())
			.body(new ResponseError(
				notFound.value(),
				notFound.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ResponseError> handleError404(CategoryNotFoundException ex) {
		return ResponseEntity
			.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(ManufacturerNotFoundException.class)
	public ResponseEntity<ResponseError> handleError404(ManufacturerNotFoundException ex) {
		return ResponseEntity
			.status(notFound.value())
			.body(new ResponseError(
				notFound.value(),
				notFound.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ResponseError> handleError404(ProductNotFoundException ex) {
		return ResponseEntity
			.status(notFound.value())
			.body(new ResponseError(
				notFound.value(),
				notFound.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseError> handleError400(MethodArgumentNotValidException ex) {
		var fields = ex.getFieldErrors().stream()
			.collect(Collectors.toMap(
				f -> f.getField().toString(), f -> f.getDefaultMessage()));
		
		return ResponseEntity
			.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
				fields));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError400(HandlerMethodValidationException ex) {
		return ResponseEntity
			.badRequest()
			.body(new ResponseErrorWithoutMessage(
				badRequest.value(),
				badRequest.getReasonPhrase()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseError> handlerErro400(IllegalArgumentException ex) {
		return ResponseEntity
			.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseError> handleError400(HttpMessageNotReadableException ex) {
		return ResponseEntity
			.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
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
			.status(unsupportedMediaType.value())
			.body(new ResponseError(
				unsupportedMediaType.value(),
				unsupportedMediaType.getReasonPhrase(),
				message));
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<?> handleError401(InvalidTokenException ex) {
		log.debug("INVALID TOKEN EXCEPTION MESSAGE: {}", ex.getMessage());
		return ResponseEntity
			.status(unauthorized.value())
			.body(new ResponseError(
				unauthorized.value(),
				unauthorized.getReasonPhrase(),
				InvalidTokenException.DEFAULT_MESSAGE));
	}

	@ExceptionHandler(CustomForbiddenException.class)
	public ResponseEntity<?> handleError403(CustomForbiddenException ex) {
		return ResponseEntity
			.status(forbidden.value())
			.body(new ResponseError(
				forbidden.value(),
				forbidden.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError500(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity
			.internalServerError()
			.body(new ResponseErrorWithoutMessage(
				internalServerError.value(),
				internalServerError.getReasonPhrase()));
	}

	private record ResponseError(int status, String error, Object message) {}
	private record ResponseErrorWithoutMessage(int status, Object error) {}
}