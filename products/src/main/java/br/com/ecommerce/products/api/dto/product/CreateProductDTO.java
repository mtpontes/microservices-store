package br.com.ecommerce.products.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {

	@NotBlank
	private String name;

	private String description;

	private String specs;

	@NotNull
	private Long categoryId;

	@NotNull
	private Long manufacturerId;
}