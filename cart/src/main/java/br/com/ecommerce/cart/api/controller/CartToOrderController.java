package br.com.ecommerce.cart.api.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.cart.api.client.OrderClient;
import br.com.ecommerce.cart.api.dto.order.OrderDataDTO;
import br.com.ecommerce.cart.api.openapi.ICartToOrderController;
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
public class CartToOrderController implements ICartToOrderController {

    private final CartService service;
    private final OrderClient orderClient;
    

    @PostMapping
    public ResponseEntity<OrderDataDTO> create(
        @AuthenticationPrincipal @Valid @NotNull UserDetailsImpl user,
        @RequestBody @Valid @NotEmpty Set<String> requestBody
    ) {
        Assert.isTrue(
            requestBody.stream().anyMatch(productId -> !productId.isBlank()), 
            "List of selected products has one or more blank references");
            
        Cart cart = service.getUserCart(user.getId());
        Set<Product> chosenProducts = service.selectProductsFromCart(cart, requestBody);
        OrderDataDTO orderData = orderClient.createOrder(user.getId(), chosenProducts);
        service.removeSelectedProducts(cart, chosenProducts);
        return ResponseEntity.ok(orderData);
    }
}