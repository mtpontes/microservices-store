package br.com.ecommerce.products.api.controller.product;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.api.dto.product.DataProductPriceDTO;
import br.com.ecommerce.products.api.dto.product.DataProductStockDTO;
import br.com.ecommerce.products.api.dto.product.ProductUnitsRequestedDTO;
import br.com.ecommerce.products.business.service.ProductService;
import br.com.ecommerce.products.infra.entity.product.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/internal/products")
public class InternalProductController {

	private final ProductService service;


	@PostMapping("/stocks")
	public ResponseEntity<List<DataProductStockDTO>> verifyStocks(
		@RequestBody @Valid @NotEmpty List<ProductUnitsRequestedDTO> dto
	) {
		List<Product> outOfStock = service.checkWichProductsExceedsStock(dto);
		
		List<DataProductStockDTO> responseBody = Collections.emptyList();
		if (outOfStock.isEmpty()) return ResponseEntity.ok(responseBody);
		
		responseBody = outOfStock.stream()
			.map(DataProductStockDTO::new)
			.toList();
		
		return ResponseEntity
			.status(HttpStatus.MULTI_STATUS)
			.body(responseBody);
	}

	@GetMapping("/prices")
	public ResponseEntity<List<DataProductPriceDTO>> getPrices(@RequestParam("productIds") List<Long> productIds) {
		return ResponseEntity.ok(service.getAllProductsByListOfIds(productIds));
	}
}