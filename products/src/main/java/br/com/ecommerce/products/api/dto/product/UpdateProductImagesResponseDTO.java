package br.com.ecommerce.products.api.dto.product;

import br.com.ecommerce.products.infra.entity.product.Images;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductImagesResponseDTO {

	private Long id;
	private String name;
	private Images images;
}