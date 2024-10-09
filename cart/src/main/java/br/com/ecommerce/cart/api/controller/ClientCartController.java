package br.com.ecommerce.cart.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.cart.api.dto.cart.AnonCartRefereceDTO;
import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.openapi.IClientCartController;
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
public class ClientCartController implements IClientCartController {

    private final CartService service;
    private final CartViewModel cartViewModel;


    @PostMapping
    public ResponseEntity<CartDTO> create(
        @AuthenticationPrincipal UserDetailsImpl user,
        UriComponentsBuilder uriBuilder
    ) {
        CartDTO responseBody = service.createCart(user.getId());

        var uri = uriBuilder
            .path("/carts/{cartId}")
            .buildAndExpand(responseBody.getId())
            .toUri();
        return ResponseEntity.created(uri).body(responseBody);
    }

    @GetMapping
    public ResponseEntity<CartDTO> get(@AuthenticationPrincipal UserDetailsImpl user) {
        Cart cart = this.service.getUserCart(user.getId());
        return ResponseEntity.ok(this.cartViewModel.getCartData(cart));
    }

    @PutMapping("/merge")
    public ResponseEntity<CartDTO> mergeCarts(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @Valid AnonCartRefereceDTO requestBody
    ) {
        Cart cart = this.service.mergeCart(user.getId(), requestBody.getAnonCartId());
        return ResponseEntity.ok(this.cartViewModel.getCartData(cart));
    }

    @PutMapping
    public ResponseEntity<CartDTO> updateUnit(
        @AuthenticationPrincipal UserDetailsImpl user,
        @RequestBody @Valid UpdateCartProductDTO requestBody
    ) {
        Cart cart = this.service.changeProductUnit(user.getId(), requestBody);
        return ResponseEntity.ok(this.cartViewModel.getCartData(cart));
    }
}