package br.com.ecommerce.orders.api.dto.product;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class ProductOutOfStockDTO {
	
	@JsonAlias("productId")
	private String id;

	private String name;

	@JsonAlias("unit")
	private Integer stock;
}