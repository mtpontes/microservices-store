package br.com.ecommerce.products.infra.exception.exceptions;

public class ManufacturerNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Manufacturer not found";

    public ManufacturerNotFoundException() {
        super(defaultMessage);
    }
}