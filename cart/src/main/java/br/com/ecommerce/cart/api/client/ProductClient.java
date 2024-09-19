package br.com.ecommerce.cart.api.client;

import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ecommerce.cart.api.dto.product.InternalProductDataDTO;

@FeignClient(value = "products-ms")
public interface ProductClient {

	@GetMapping(
		value = "/internal/products/prices",
		headers = {"Content-Type: application/json"})
	Map<String, InternalProductDataDTO> getPrices(@RequestParam("productIds") Set<String> productsId);

	@GetMapping(
		value = "/internal/products/prices/{productId}",
		headers = {"Content-Type: application/json"})
	InternalProductDataDTO getPrice(@PathVariable("productId") String productId);
}