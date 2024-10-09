package br.com.ecommerce.cart.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.openapi.IAnonymousCartController;
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
public class AnonymousCartController implements IAnonymousCartController {

    private final CartService service;
    private final CartViewModel cartViewModel;
    

    @PostMapping
    public ResponseEntity<CartDTO> create(
        @RequestBody @Valid UpdateCartProductDTO requestBody,
        UriComponentsBuilder uriBuilder
    ) {
        Cart cart = service.createAnonCart(requestBody);
        CartDTO responseBody = cartViewModel.getCartData(cart);

        var uri = uriBuilder
            .path("/anonymous/carts/{anonCartId}")
            .buildAndExpand(responseBody.getId())
            .toUri();
        return ResponseEntity.created(uri).body(responseBody);
    }

    @GetMapping
    public ResponseEntity<CartDTO> get(@RequestHeader(name = "X-anon-cart-id") String userId) {
        Cart cart = this.service.getCart(userId);
        return ResponseEntity.ok(cartViewModel.getCartData(cart));
    }

    @PutMapping
    public ResponseEntity<CartDTO> updateUnit(
        @RequestHeader(name = "X-anon-cart-id") String userId,
        @RequestBody @Valid UpdateCartProductDTO requestBody
    ) {
        Cart cart = this.service.changeProductUnit(userId, requestBody);
        return ResponseEntity.ok(cartViewModel.getCartData(cart));
    }
}