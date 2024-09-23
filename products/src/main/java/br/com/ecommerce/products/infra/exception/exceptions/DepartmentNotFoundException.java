package br.com.ecommerce.products.infra.exception.exceptions;

public class DepartmentNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Department not found";

    public DepartmentNotFoundException() {
        super(defaultMessage);
    }
}