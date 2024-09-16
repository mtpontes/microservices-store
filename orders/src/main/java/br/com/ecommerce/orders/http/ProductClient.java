package br.com.ecommerce.orders.http;

import java.util.List;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ecommerce.orders.dto.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.dto.product.ProductDTO;
import br.com.ecommerce.orders.exception.ProductOutOfStockDTO;

@FeignClient(value = "products-ms")
public interface ProductClient {

	@PostMapping(
		value = "/internal/products/stocks",
		headers = {"Content-Type: application/json"})
	ResponseEntity<List<ProductOutOfStockDTO>> verifyStocks(@RequestBody List<ProductDTO> products);

	@GetMapping(
		value = "/internal/products/prices",
		headers = {"Content-Type: application/json"})
	ResponseEntity<Set<ProductAndPriceDTO>> getPrices(@RequestParam("productIds") Set<Long> productsId);
}