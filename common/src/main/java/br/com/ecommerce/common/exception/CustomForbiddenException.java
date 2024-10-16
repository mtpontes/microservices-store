package br.com.ecommerce.common.exception;

public class CustomForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_MESSAGE = "Access denied";

	public CustomForbiddenException() {
		super(DEFAULT_MESSAGE);
	}

	public CustomForbiddenException(String message) {
		super(message);
	}
}