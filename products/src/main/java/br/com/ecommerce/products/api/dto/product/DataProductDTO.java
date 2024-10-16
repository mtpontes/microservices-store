package br.com.ecommerce.products.api.dto.product;

import java.io.Serializable;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.infra.entity.product.Images;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataProductDTO implements Serializable {

	private Long id;
	private String name;
	private String description;
	private String specs;
	private SimplePriceDataDTO price;
	private DataStockDTO stock;
	private Images images;
	private SimpleDataCategoryDTO category;
	private SimpleDataManufacturerDTO manufacturer;
}