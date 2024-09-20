package br.com.ecommerce.cart.infra.exception.handlers;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.ecommerce.cart.api.dto.exception.ResponseError;
import br.com.ecommerce.cart.api.dto.exception.ResponseErrorWithouMessage;
import br.com.ecommerce.cart.infra.exception.exceptions.CartNotFoundException;
import br.com.ecommerce.cart.infra.exception.exceptions.EmptyCartException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

	private final String INPUT_VALIDATION_ERROR = "Input validation error";
	private final String INTERNAL_SERVER_ERROR_MESSAGE = "An internal error occurred";

	private final HttpStatus notFound = HttpStatus.NOT_FOUND;
	private final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
	private final HttpStatus unsupportedMediaType = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
	private final HttpStatus internalServerError= HttpStatus.INTERNAL_SERVER_ERROR;


	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ResponseErrorWithouMessage> handleError404(NoResourceFoundException ex) {
		return ResponseEntity.status(notFound.value())
			.body(new ResponseErrorWithouMessage(
				notFound.value(),
				notFound.getReasonPhrase()));
	}

	@ExceptionHandler(CartNotFoundException.class)
	public ResponseEntity<ResponseErrorWithouMessage> handlerError404(CartNotFoundException ex) {
		return ResponseEntity.status(notFound.value())
		.body(new ResponseErrorWithouMessage(
			notFound.value(),
			notFound.getReasonPhrase()));
	}

	@ExceptionHandler(EmptyCartException.class)
	public ResponseEntity<ResponseError> handlerError400(EmptyCartException ex) {
		return ResponseEntity.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseError> handleError400(MethodArgumentNotValidException ex) {
		var fields = ex.getFieldErrors().stream()
			.collect(Collectors.toMap(f -> f.getField().toString(), f -> f.getDefaultMessage()));
		return ResponseEntity.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
				fields));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ResponseError> handlerErro400(HandlerMethodValidationException ex) {
		return ResponseEntity
			.badRequest()
			.body(new ResponseError(
				badRequest.value(), 
				badRequest.getReasonPhrase(),
				INPUT_VALIDATION_ERROR));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseError> handlerErro400(IllegalArgumentException ex) {
		return ResponseEntity.badRequest()
			.body(new ResponseError(
				badRequest.value(), 
				badRequest.getReasonPhrase(),
				ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseError> handlerErro400(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
			.body(new ResponseError(
				badRequest.value(), 
				badRequest.getReasonPhrase(),
				"Malformed or unexpected json format"));
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ResponseError> handleError400(MissingRequestHeaderException ex) {
		String message = ex.getMessage();
		if (message.contains("X-anon-cart-id")) 
			return ResponseEntity.badRequest()
				.body(new ResponseError(
					badRequest.value(), 
					badRequest.getReasonPhrase(),
					ex.getMessage()));
		return ResponseEntity.internalServerError()
			.body(new ResponseError(
				internalServerError.value(),
				internalServerError.getReasonPhrase(),
				"Missing header"));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ResponseError> handlerErro415(HttpMediaTypeNotSupportedException ex) {
		String unsupported = Optional.ofNullable(ex.getContentType())
			.map(m -> m.getType() +"/"+ m.getSubtype())
			.orElse("unknown");

		String supported = ex.getSupportedMediaTypes().stream()
			.map(mediaType -> mediaType.getType() + "/" + mediaType.getSubtype())
			.collect(Collectors.joining(", "));
		String message = String.format("Unsupported media type '%s'. Supported media types are: %s", unsupported, supported);
		return ResponseEntity.status(unsupportedMediaType.value())
			.body(new ResponseError(
				unsupportedMediaType.value(),
				unsupportedMediaType.getReasonPhrase(), 
				message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseError> handleError500(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity.internalServerError()
			.body(new ResponseError(
				internalServerError.value(),
				internalServerError.getReasonPhrase(),
				INTERNAL_SERVER_ERROR_MESSAGE));
	}
}