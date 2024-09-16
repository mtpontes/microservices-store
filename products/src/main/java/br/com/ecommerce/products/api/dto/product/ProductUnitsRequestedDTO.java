package br.com.ecommerce.products.api.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitsRequestedDTO {

	@NotNull
	private Long id;

	@NotNull
	private Integer unit;
}