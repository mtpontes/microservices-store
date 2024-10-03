package br.com.ecommerce.common.jwt;

public class InvalidTokenException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT = "Invalid token";

	public InvalidTokenException() {
		super(DEFAULT);
	}
}