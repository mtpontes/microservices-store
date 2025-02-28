package br.com.ecommerce.payment.exception;

import static org.springframework.http.HttpStatus.*;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final String HTTP_MESSAGE_NOT_READABLE_EXCEPTION = "Malformed or unexpected json format";

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError404(NoResourceFoundException ex) {
		return ResponseEntity
				.status(NOT_FOUND.value())
				.body(new ResponseErrorWithoutMessage(
						NOT_FOUND.value(),
						NOT_FOUND.getReasonPhrase()));
	}

	@ExceptionHandler(PaymentNotFoundException.class)
	public ResponseEntity<ResponseError> handleError400(PaymentNotFoundException ex) {
		return ResponseEntity
				.status(BAD_REQUEST.value())
				.body(new ResponseError(
						BAD_REQUEST.value(),
						BAD_REQUEST.getReasonPhrase(),
						ex.getMessage()));
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
	public ResponseEntity<ResponseError> handlerErro400(IllegalArgumentException ex) {
		return ResponseEntity
				.badRequest()
				.body(new ResponseError(
						BAD_REQUEST.value(),
						BAD_REQUEST.getReasonPhrase(),
						ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseError> handleError400(HttpMessageNotReadableException ex) {
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
				.map(m -> m.getType() + "/" + m.getSubtype())
				.orElse("unknown");

		String supported = ex.getSupportedMediaTypes().stream()
				.map(mediaType -> mediaType.getType() + "/" + mediaType.getSubtype())
				.collect(Collectors.joining(", "));
		String message = String.format("Unsupported media type '%s'. Supported media types are: %s", unsupported,
				supported);

		return ResponseEntity
				.status(UNSUPPORTED_MEDIA_TYPE.value())
				.body(new ResponseError(
						UNSUPPORTED_MEDIA_TYPE.value(),
						UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
						message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError500(Exception ex) {
		return ResponseEntity
				.internalServerError()
				.body(new ResponseErrorWithoutMessage(
						INTERNAL_SERVER_ERROR.value(),
						INTERNAL_SERVER_ERROR.getReasonPhrase()));
	}

	private record ResponseError(int status, String error, Object message) {
	}

	private record ResponseErrorWithoutMessage(int status, Object error) {
	}
}