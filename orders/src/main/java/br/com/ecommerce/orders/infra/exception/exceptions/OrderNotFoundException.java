package br.com.ecommerce.orders.infra.exception.exceptions;

public class OrderNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Order not found";

    public OrderNotFoundException() {
        super(defaultMessage);
    }
}