package br.com.ecommerce.cart.api.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.cart.api.client.OrderClient;
import br.com.ecommerce.cart.api.dto.order.OrderDataDTO;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
        @RequestHeader(name = "X-auth-user-id", required = false) String userId,
        @RequestBody @Valid @NotEmpty Set<String> requestBody
    ) {
        Set<Product> chosenProducts = service.cartToOrder(userId, requestBody);
        OrderDataDTO orderData = orderClient.createOrder(userId, chosenProducts);
        return ResponseEntity.ok(orderData);
    }
}