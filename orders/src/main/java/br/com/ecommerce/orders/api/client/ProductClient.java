package br.com.ecommerce.orders.api.client;

import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ecommerce.orders.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.orders.api.dto.product.ProductAndUnitDTO;
import br.com.ecommerce.orders.api.dto.product.ProductOutOfStockDTO;

@FeignClient(value = "products-ms")
public interface ProductClient {

	@PostMapping(
		value = "/internal/products/stocks",
		headers = {"Content-Type: application/json"})
	Set<ProductOutOfStockDTO> verifyStocks(@RequestBody Set<ProductAndUnitDTO> products);

	@GetMapping(
		value = "/internal/products/prices",
		headers = {"Content-Type: application/json"})
	Map<String, InternalProductDataDTO> getPrices(@RequestParam("productIds") Set<String> productsId);
}