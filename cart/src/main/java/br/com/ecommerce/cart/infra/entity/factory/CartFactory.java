package br.com.ecommerce.cart.infra.entity.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;

@Component
public class CartFactory {

    public Cart createUserCart(String userId) {
        return new Cart(userId);
    }

    public Cart createAnonymousCart(Product product) {
        return new Cart(product);
    }
}