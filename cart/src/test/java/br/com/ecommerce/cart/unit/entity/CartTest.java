package br.com.ecommerce.cart.unit.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;

public class CartTest {

    private final String validUserId = "1";
    private final Set<Product> emptyProductSet = Collections.emptySet();

    
    @Test
    void createCartTest() {
        assertDoesNotThrow(() -> {
            Cart cart = new Cart(validUserId);
            assertFalse(cart.isAnon());
        });

        assertDoesNotThrow(() -> {
            Cart cart = new Cart(new Product());
            assertTrue(cart.isAnon());
        });
    }
    
    @Test
    void userIdTest() {
        Cart cart = new Cart(validUserId);
        assertEquals(validUserId, cart.getId());
    }

    @Test
    void anonymousUserIdTest() {
        int expectedPiecesOfId = 5;
        Cart cart = new Cart(new Product());
        assertEquals(expectedPiecesOfId, cart.getId().split("-").length);
    }

    @Test
    void addProductTest_withProducts() {
        // arrange
        Cart cart = new Cart(validUserId);
        Set<Product> products = this.createProductSet();
        Integer expectedSetSize = products.size();

        // act
        cart.addProducts(products);

        // assert
        assertEquals(expectedSetSize, cart.getProducts().size());
    }

    @Test
    void addProductTest_withoutProducts() {
        // arrange
        Cart cart = new Cart(validUserId);

        // act
        assertDoesNotThrow(() -> cart.addProducts(emptyProductSet));
    }

    @Test
    void removeProductTest_withProducts() {
        // arrange
        Set<Product> products = this.createProductSet();
        Cart cart = new Cart(products.iterator().next());
        cart.addProducts(products);
        Integer expectedSetSize = products.size() - 1;

        // act
        cart.removeProduct(products.stream().findFirst().get());

        // assert
        assertEquals(expectedSetSize, cart.getProducts().size());
    }


    private Set<Product> createProductSet() {
        return IntStream.range(0, 3)
            .mapToObj(p -> {
                Product product = new Product();
                ReflectionTestUtils.setField(product, "id", String.valueOf(p));
                ReflectionTestUtils.setField(product, "unit", 1);
                return product;
            }).collect(Collectors.toSet());
    }
}