package br.com.ecommerce.accounts.infra.exception;

public class FailedCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FailedCredentialsException(String message) {
		super(message);
	}
	
	public FailedCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}
}