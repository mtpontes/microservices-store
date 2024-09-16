package br.com.ecommerce.products.infra.entity.tools.interfaces;

public interface Validator<T> {
    void validate(T param);
}