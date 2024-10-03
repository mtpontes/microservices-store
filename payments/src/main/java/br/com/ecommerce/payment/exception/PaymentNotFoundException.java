package br.com.ecommerce.payment.exception;

public class PaymentNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Payment not found";

    public PaymentNotFoundException() {
        super(defaultMessage);
    }
}