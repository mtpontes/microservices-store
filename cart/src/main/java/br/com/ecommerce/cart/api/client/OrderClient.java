package br.com.ecommerce.cart.api.client;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import br.com.ecommerce.cart.api.dto.order.OrderDataDTO;
import br.com.ecommerce.cart.infra.entity.Product;

@FeignClient(value = "orders-ms")
public interface OrderClient {

    @PostMapping(
        value = "/internal/orders",
        headers = {"Content-Type: application/json"})
    OrderDataDTO createOrder(
        @RequestHeader(name = "X-auth-user-id") String userId,
        @RequestBody Set<Product> products);
}