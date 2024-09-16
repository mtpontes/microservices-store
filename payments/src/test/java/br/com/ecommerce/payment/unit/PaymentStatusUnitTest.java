package br.com.ecommerce.payment.unit;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.payment.model.PaymentStatus;


class PaymentStatusUnitTest {

	@Test
	void fromStringTest01() {
		assertThrows(IllegalArgumentException.class, () -> PaymentStatus.fromString("non-existent"));
	}

	@Test
	void fromStringTest02() {
		var values = Arrays.asList(PaymentStatus.values());
		values.forEach(value -> {
			assertDoesNotThrow(() -> PaymentStatus.fromString(value.toString()));
		});
	}
}