package br.com.ecommerce.accounts.model.interfaces;

public interface Validator<T> {
    void validate(T obj);
}