package br.com.ecommerce.products.api.controller.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.api.dto.product.CreateProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductStockDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.EndOfPromotionDTO;
import br.com.ecommerce.products.api.dto.product.UpdatePriceDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductPriceResponseDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductResponseDTO;
import br.com.ecommerce.products.business.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

	private final ProductService service;


	@PostMapping
	public ResponseEntity<DataProductDTO> createProduct(
		@RequestBody @Valid CreateProductDTO dto, 
		UriComponentsBuilder uriBuilder
	) {
		DataProductDTO responseBody = service.createProduct(dto);
		var uri = uriBuilder
			.path("/products/{productId}")
			.buildAndExpand(responseBody.getId())
			.toUri();
		return ResponseEntity.created(uri).body(responseBody);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<UpdateProductResponseDTO> updateProduct(
		@PathVariable Long productId, 
		@RequestBody UpdateProductDTO dto
	) {
		return ResponseEntity.ok(service.updateProductData(productId, dto));
	}

	@PutMapping("/{productId}/stocks")
	public ResponseEntity<DataProductStockDTO> updateStock(
		@PathVariable Long productId, 
		@RequestBody @Valid DataStockDTO dto
	) {
		return ResponseEntity.ok(service.updateStockByProductId(productId, dto));
	}

	@PutMapping("/{productId}/prices")
	public ResponseEntity<UpdateProductPriceResponseDTO> updatePrice(
		@PathVariable Long productId, 
		@RequestBody UpdatePriceDTO dto
	) {
		return ResponseEntity.ok(service.updateProductPrice(productId, dto));
	}

	@PutMapping("/{productId}/prices/switch-to-promotional")
	public ResponseEntity<UpdateProductPriceResponseDTO> switchCurrentPriceToPromotionalPrice(
		@PathVariable Long productId,
		@RequestBody @Valid EndOfPromotionDTO requestBody
	) {
		return ResponseEntity.ok(service.switchCurrentPriceToPromotional(productId, requestBody.getEndOfPromotion()));
	}

	@PutMapping("/{productId}/prices/switch-to-original")
	public ResponseEntity<UpdateProductPriceResponseDTO> switchCurrentPriceToOriginalPrice(
		@PathVariable Long productId
	) {
		return ResponseEntity.ok(service.switchCurrentPriceToOriginal(productId));
	}
}