package br.com.ecommerce.products.api.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class StockWriteOffDTO {

	@NotNull
	private Long productId;

	@NotNull
	private Integer unit;

	public StockWriteOffDTO(Long productId, Integer unit) {
		this.productId = productId;
		this.unit = Math.negateExact(unit);
	} 
}