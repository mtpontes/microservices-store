package br.com.ecommerce.products.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductResponseDTO {

	private Long id;
	private String name;
	private String description;
	private String specs;
}