package br.com.ecommerce.products.infra.entity.tools.interfaces;

public interface Formatter<T> {
    String format(T param);
}