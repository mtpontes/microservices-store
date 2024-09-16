package br.com.ecommerce.accounts.infra.exception;

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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private final String CREDENTIALS_ERROR_MESSAGE = "Bad credentials";
	private final String ENTITY_NOT_FOUND_EXCEPTION = "User not found";
	private final String METHOD_ARGUMENT_NOT_VALID_MESSAGE = "Input validation error";
	private final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
	
	
	@ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleError404(
		NoResourceFoundException ex
	) {
    	return ResponseEntity.notFound().build();
    }

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleError401(
		EntityNotFoundException ex
	) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ErrorMessage(
				HttpStatus.UNAUTHORIZED.value(),
				ENTITY_NOT_FOUND_EXCEPTION));
	}

	@ExceptionHandler(FailedCredentialsException.class)
	public ResponseEntity<ErrorMessage> handleError401(
		FailedCredentialsException ex
	) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ErrorMessage(
				HttpStatus.UNAUTHORIZED.value(), 
				CREDENTIALS_ERROR_MESSAGE));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessageWithFields> handleError400(MethodArgumentNotValidException ex) {
		var fields = ex.getFieldErrors().stream()
			.collect(Collectors.toMap(
				f -> f.getField().toString(), f -> f.getDefaultMessage()));

		return ResponseEntity.badRequest()
			.body(new ErrorMessageWithFields(
				HttpStatus.BAD_REQUEST.value(),
				METHOD_ARGUMENT_NOT_VALID_MESSAGE,
				fields));
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorMessage> handlerErro400IllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.badRequest()
			.body(new ErrorMessage(
				HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleError400HttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
			.body(new ErrorMessage(
				HttpStatus.BAD_REQUEST.value(), "Malformed or unexpected json format"));
    }

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handlerErro415(HttpMediaTypeNotSupportedException ex) {
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

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
			.body(new ErrorMessage(
				HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), message));
    }

	@ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorMessage> handlerMissingRequestHeaderException(MissingRequestHeaderException ex) {
		String headerName = ex.getHeaderName();

		if (headerName.equalsIgnoreCase("X-auth-user-id"))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorMessage(401, headerName));

        return this.handleError500(ex);
    }

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleError500(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity
			.internalServerError()
			.body(new ErrorMessage(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				INTERNAL_SERVER_ERROR_MESSAGE));
	}
	
	private record ErrorMessage(int status, Object error) {}
	private record ErrorMessageWithFields(int status, String error, Object fields) {}
}