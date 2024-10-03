package br.com.ecommerce.cart.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.mapper.CartMapper;
import br.com.ecommerce.cart.api.mapper.ProductMapper;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.infra.entity.factory.CartFactory;
import br.com.ecommerce.cart.infra.exception.exceptions.CartNotFoundException;
import br.com.ecommerce.cart.infra.exception.exceptions.EmptyCartException;
import br.com.ecommerce.cart.infra.repository.CartRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRespository;
    private final CartFactory cartFactory;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;


    @Transactional
    public CartDTO createCart(String userId) {
        if (cartRespository.existsById(userId)) throw new IllegalArgumentException("Existent cart");
        Cart mapped = this.cartFactory.createUserCart(userId);
        this.cartRespository.save(mapped);
        return cartMapper.toCartDTO(mapped, Collections.emptyList(), BigDecimal.ZERO);
    }

    @Transactional
    public Cart createAnonCart(UpdateCartProductDTO data) {
        Product product = productMapper.toProduct(data);
        Cart mapped = this.cartFactory.createAnonymousCart(product);
        return this.cartRespository.save(mapped);
    }

    public Cart getCart(String cartId) {
        return cartRespository.findById(cartId)
            .orElseThrow(CartNotFoundException::new);
    }

    @Transactional
    public Cart mergeCart(String userId, String anonCartId) {
        Cart userCart = cartRespository.findById(userId)
            .orElse(new Cart(userId));
            
        Set<Product> anonCartProducts = cartRespository.findById(anonCartId)
            .orElseThrow(CartNotFoundException::new)
            .getProducts();
        userCart.addProducts(anonCartProducts);
        
        cartRespository.deleteById(anonCartId);
        return cartRespository.save(userCart);
    }

    @Transactional
    public Cart changeProductUnit(String userId, UpdateCartProductDTO update) {
        return cartRespository.findById(userId)
            .map(cart -> cart.getProducts().stream()
                .filter(product -> product.getId().equals(product.getId()))
                .findFirst()
                .map(product -> {
                    boolean isZero = (product.getUnit() + update.getUnit()) <= 0;
                    if (isZero) cart.removeProduct(product);
                    if (!isZero) product.addUnit(update.getUnit());
                    return cartRespository.save(cart);
                })
                .orElseGet(() -> {
                    Product completeProduct = productMapper.toProduct(update);
                    cart.addProduct(completeProduct);
                    return cartRespository.save(cart);
                }))
            .orElseThrow(CartNotFoundException::new);
    }

    public Cart getUserCart(String cartId) {
        Cart cart = this.getCart(cartId);
        if (cart.isAnon()) throw new CartNotFoundException();
        return cart;
    }

    @Transactional
    public Set<Product> selectProductsFromCart(Cart cart, Set<String> productIds) {
        // validate list of ids
        boolean isBlankSet = productIds.stream().filter(string -> !string.isBlank()).count() <= 0L;
        if (isBlankSet) throw new IllegalArgumentException("List of selected products is empty");
        
        // recover and validate cart
        if (cart.isAnon()) throw new CartNotFoundException();
        if (cart.getProducts().isEmpty()) throw new EmptyCartException();

        // select products for order
        Set<Product> chosenProducts = cart.getProducts().stream()
            .filter(product -> productIds.stream().anyMatch(choice -> choice.equalsIgnoreCase(product.getId())))
            .collect(Collectors.toSet());
        return chosenProducts;
    }

    @Transactional
    public void removeSelectedProducts(Cart cart, Set<Product> chosenProducts) {
        chosenProducts.forEach(selected -> cart.removeProduct(selected));
        cartRespository.save(cart);
    }
}