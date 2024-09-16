package br.com.ecommerce.orders.infra.exception;

import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.orders.api.dto.product.ProductOutOfStockDTO;
import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private List<ProductOutOfStockDTO> products = new ArrayList<>();

	public OutOfStockException(String message, List<ProductOutOfStockDTO> products) {
		super(message);
		this.products = products;
	}
}