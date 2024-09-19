package br.com.ecommerce.cart.infra.entity.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.cart.infra.entity.Product;

@Component
public class ProductFactory {

    public Product createProduct(String id, Integer unit) {
        return new Product(id, unit);
    }
}