package br.com.ecommerce.common.exception;

public class InvalidTokenException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_MESSAGE = "Invalid token";

	public InvalidTokenException() {
		super(DEFAULT_MESSAGE);
	}

	public InvalidTokenException(String message) {
		super(message);
	}
}