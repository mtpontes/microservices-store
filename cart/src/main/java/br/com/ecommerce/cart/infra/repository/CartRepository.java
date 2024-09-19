package br.com.ecommerce.cart.infra.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.ecommerce.cart.infra.entity.Cart;

public interface CartRepository extends MongoRepository<Cart, String> {
}