package br.com.ecommerce.cart.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.cart.api.dto.cart.AnonCartRefereceDTO;
import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.view.CartViewModel;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.service.CartService;
import br.com.ecommerce.common.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/carts")
@AllArgsConstructor
public class ClientCartController {

    private final CartService service;
    private final CartViewModel cartViewModel;


    @PostMapping
    public CartDTO create(@AuthenticationPrincipal UserDetailsImpl user) {
        return service.createCart(user.getId().toString());
    }

    @GetMapping
    public CartDTO get(@AuthenticationPrincipal UserDetailsImpl user) {
        Cart cart = this.service.getUserCart(user.getId().toString());
        return this.cartViewModel.getCartData(cart);
    }

    @PutMapping("/merge")
    public CartDTO mergeCarts(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @Valid AnonCartRefereceDTO requestBody
    ) {
        Cart cart = this.service.mergeCart(user.getId().toString(), requestBody.getAnonCartId());
        return this.cartViewModel.getCartData(cart);
    }

    @PutMapping
    public CartDTO updateUnit(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @Valid UpdateCartProductDTO requestBody
    ) {
        Cart cart = this.service.changeProductUnit(user.getId().toString(), requestBody);
        return this.cartViewModel.getCartData(cart);
    }
}