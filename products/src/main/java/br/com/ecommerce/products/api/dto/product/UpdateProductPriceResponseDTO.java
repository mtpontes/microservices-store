package br.com.ecommerce.products.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductPriceResponseDTO {

	private Long id;
	private String name;
	private CompletePriceDataDTO price;
}