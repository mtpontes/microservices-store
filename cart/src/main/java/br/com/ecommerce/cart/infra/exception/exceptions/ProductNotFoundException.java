package br.com.ecommerce.cart.infra.exception.exceptions;

public class ProductNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Product not found";

    public ProductNotFoundException() {
        super(defaultMessage);
    }
}