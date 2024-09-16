package br.com.ecommerce.orders.infra.exception;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record ProductOutOfStockDTO(
		
	@JsonAlias("productId")
	Long id, 

	String name, 

	@JsonAlias("unit")
	Integer stock
) {}