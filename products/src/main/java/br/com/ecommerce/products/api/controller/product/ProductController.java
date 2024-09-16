package br.com.ecommerce.products.api.controller.product;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import br.com.ecommerce.products.business.service.ProductService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService service;


	@GetMapping
	public ResponseEntity<Page<DataProductDTO>> getAll(
		@PageableDefault(size = 10) Pageable pageable,
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) BigDecimal minPrice,
		@RequestParam(required = false) BigDecimal maxPrice,
		@RequestParam(required = false) String manufacturer
	) {
		return ResponseEntity.ok(service.getAllProductWithParams(
			name, 
			category, 
			minPrice, 
			maxPrice, 
			manufacturer,
			pageable
			));
	}

	@GetMapping("/{productId}")
	public ResponseEntity<DataProductDTO> getProduct(@PathVariable Long productId) {
		return ResponseEntity.ok(service.getProduct(productId));
	}
}