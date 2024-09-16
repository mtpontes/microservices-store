package br.com.ecommerce.orders.exception;

import java.util.ArrayList;
import java.util.List;

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