package br.com.ecommerce.cart.infra.exception.exceptions;

public class EmptyCartException extends RuntimeException {
    private static final String defaultMessage = "The cart is empty";

    public EmptyCartException() {
        super(defaultMessage);
    }
}