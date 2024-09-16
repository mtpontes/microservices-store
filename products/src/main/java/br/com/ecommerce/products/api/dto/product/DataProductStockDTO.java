package br.com.ecommerce.products.api.dto.product;

import br.com.ecommerce.products.infra.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter 
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataProductStockDTO {

	private Long productId;
	private String name;
	private Integer unit;

	public DataProductStockDTO(Product p) {
		this.productId = p.getId();
		this.name = p.getName();
		this.unit = p.getStock().getUnit();
	}
}