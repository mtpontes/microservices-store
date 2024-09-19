package br.com.ecommerce.cart.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.view.CartViewModel;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/anonymous/carts")
@AllArgsConstructor
public class AnonymousCartController {

    private final CartService service;
    private final CartViewModel cartViewModel;
    

    @PostMapping
    public CartDTO create(@RequestBody @Valid UpdateCartProductDTO requestBody) {
        Cart cart = service.createAnonCart(requestBody);
        return cartViewModel.getCartData(cart);
    }

    @GetMapping
    public CartDTO get(@RequestHeader(name = "X-anon-cart-id") String userId) {
        Cart cart = this.service.getCart(userId);
        return cartViewModel.getCartData(cart);
    }

    @PutMapping
    public CartDTO updateUnit(
        @RequestHeader(name = "X-anon-cart-id") String userId,
        @RequestBody @Valid UpdateCartProductDTO requestBody
    ) {
        Cart cart = this.service.changeProductUnit(userId, requestBody);
        return cartViewModel.getCartData(cart);
    }
}