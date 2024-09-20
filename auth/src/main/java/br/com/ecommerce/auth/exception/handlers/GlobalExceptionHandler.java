package br.com.ecommerce.auth.exception.handlers;

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

import br.com.ecommerce.auth.exception.exceptions.InvalidTokenException;
import br.com.ecommerce.auth.exception.exceptions.UserNotFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

	private final HttpStatus notFound = HttpStatus.NOT_FOUND;
	private final HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
	private final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
	private final HttpStatus unsupportedMediaType = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
	private final HttpStatus internalServerError= HttpStatus.INTERNAL_SERVER_ERROR;


	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ResponseErrorWithoutMessage> handleError404(NoResourceFoundException ex) {
		return ResponseEntity
			.status(notFound.value())
			.body(new ResponseErrorWithoutMessage(
				notFound.value(),
				notFound.getReasonPhrase()));
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseError> handleError404(UserNotFoundException ex) {
		return ResponseEntity
			.status(notFound.value())
			.body(new ResponseError(
				notFound.value(), 
				notFound.getReasonPhrase(), 
				ex.getMessage()));
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ResponseError> handlerError401(InvalidTokenException ex) {
		return ResponseEntity
			.status(unauthorized.value())
			.body(new ResponseError(
				unauthorized.value(),
				unauthorized.getReasonPhrase(),
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
	public ResponseEntity<ResponseErrorWithoutMessage> handleError400(HandlerMethodValidationException ex) {
		return ResponseEntity
			.status(badRequest.value())
			.body(new ResponseErrorWithoutMessage(
				badRequest.value(),
				badRequest.getReasonPhrase()));
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
	public ResponseEntity<ResponseError> handleError400(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
			.body(new ResponseError(
				badRequest.value(),
				badRequest.getReasonPhrase(),
				"Malformed or unexpected json format"));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ResponseError> handlerErro415(HttpMediaTypeNotSupportedException ex) {
		String unsupported = Optional.ofNullable(ex.getContentType())
			.map(e -> e.getType() + "/" + e.getSubtype())
			.orElse("unknown");

		String supported = ex.getSupportedMediaTypes().stream()
			.map(mediaType -> mediaType.getType() + "/" + mediaType.getSubtype())
			.collect(Collectors.joining(", "));
			
		String message = 
			String.format("Unsupported media type '%s'. Supported media types are: %s", unsupported, supported);

		return ResponseEntity
			.status(unsupportedMediaType.value())
			.body(new ResponseError(
				unsupportedMediaType.value(),
				unsupportedMediaType.getReasonPhrase(), 
				message));
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ResponseErrorWithoutMessage> handlerMissingRequestHeaderException(
		MissingRequestHeaderException ex
	) {
		String headerName = ex.getHeaderName();
		if (headerName.equalsIgnoreCase("X-auth-user-id"))
			return ResponseEntity
				.status(unauthorized.value())
				.body(new ResponseErrorWithoutMessage(
					unauthorized.value(), 
					unauthorized.getReasonPhrase()));

        return this.handleError500(ex);
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