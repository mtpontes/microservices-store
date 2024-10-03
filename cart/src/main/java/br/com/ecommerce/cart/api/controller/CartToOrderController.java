package br.com.ecommerce.cart.api.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.cart.api.client.OrderClient;
import br.com.ecommerce.cart.api.dto.order.OrderDataDTO;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.service.CartService;
import br.com.ecommerce.common.user.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cart-to-order")
@AllArgsConstructor
public class CartToOrderController {

    private final CartService service;
    private final OrderClient orderClient;
    

    @PostMapping
    public ResponseEntity<OrderDataDTO> create(
        @AuthenticationPrincipal @Valid @NotNull UserDetailsImpl user,
        @RequestBody @Valid @NotEmpty Set<String> requestBody
    ) {
        requestBody.stream()
            .filter(productId -> !productId.isBlank())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("List of selected products is empty"));
            
        Cart cart = service.getUserCart(user.getId().toString());
        Set<Product> chosenProducts = service.selectProductsFromCart(cart, requestBody);
        OrderDataDTO orderData = orderClient.createOrder(user.getId(), chosenProducts);
        service.removeSelectedProducts(cart, chosenProducts);
        return ResponseEntity.ok(orderData);
    }
}