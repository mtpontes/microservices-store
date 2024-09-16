package br.com.ecommerce.orders.infra.configs;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import br.com.ecommerce.orders.infra.entity.OrderStatus;

public class StringToCategoryConverter implements Converter<String, OrderStatus>{

	@Override
	public OrderStatus convert(@NonNull String source) {
		try {
			return OrderStatus.fromString(source);
		
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}