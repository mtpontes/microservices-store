package br.com.ecommerce.orders.unit;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.orders.model.OrderStatus;


class OrderStatusUnitTest {

	@Test
	void fromStringTest01() {
		assertThrows(IllegalArgumentException.class, 
			() -> OrderStatus.fromString("non-existent"));
	}

	@Test
	void fromStringTest02() {
		var values = Arrays.asList(OrderStatus.values());
		values.forEach(value -> {
			assertDoesNotThrow(() -> OrderStatus.fromString(value.toString()));
		});
	}
}