package br.com.ecommerce.cart.infra.exception.exceptions;

public class CartNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Cart not found";

    public CartNotFoundException() {
        super(defaultMessage);
    }
}