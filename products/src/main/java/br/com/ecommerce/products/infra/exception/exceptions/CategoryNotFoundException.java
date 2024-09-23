package br.com.ecommerce.products.infra.exception.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Category not found";

    public CategoryNotFoundException() {
        super(defaultMessage);
    }
}