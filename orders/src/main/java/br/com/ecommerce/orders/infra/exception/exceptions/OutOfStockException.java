package br.com.ecommerce.orders.infra.exception.exceptions;

import java.util.Set;

import br.com.ecommerce.orders.api.dto.product.ProductOutOfStockDTO;
import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {
	private static final String defaultMessage = "Out of stock products";
	private Set<ProductOutOfStockDTO> products;

	public OutOfStockException(Set<ProductOutOfStockDTO> products) {
		super(defaultMessage);
		this.products = products;
	}
}