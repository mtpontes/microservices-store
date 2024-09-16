package br.com.ecommerce.accounts.unit.business.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.business.formatter.PhoneNumberFormatter;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;

public class PhoneNumberFormatterTest {

	private PhoneNumberFormatter formatter = new PhoneNumberFormatter();

	
	@Test
	@DisplayName("Unit - It should format as expected")
    void formatTest01() {
		// arrange
		String input = "47999999999";
		String expected = "+55 47 99999-9999";
		PhoneNumber phone = new PhoneNumber();
		ReflectionTestUtils.setField(phone, "value", input);

		// act
		var result = formatter.format(phone);

		// assert
		assertEquals(expected, result);
	}
	
	@Test
	@DisplayName("Unit - Should throw exception when phone number exceeds length")
    void formatTest02() {
		// arrange
		String input = "479999999999";
		PhoneNumber phone = new PhoneNumber();
		ReflectionTestUtils.setField(phone, "value", input);

		// act and assert
		assertThrows(
			IllegalArgumentException.class,
			() -> formatter.format(phone));
	}

	@Test
	@DisplayName("Unit - Should throw exception when phone number does not reach correct length")
    void formatTest03() {
		// arrange
		String input = "4799999999";
		PhoneNumber phone = new PhoneNumber();
		ReflectionTestUtils.setField(phone, "value", input);

		// act and assert
		assertThrows(
			IllegalArgumentException.class,
			() -> formatter.format(phone));
	}

	@Test
	@DisplayName("Unit - Should throw exception when String contains letters")
    void formatTest04() {
		// arrange
		String input = "4799999999B";
		PhoneNumber phone = new PhoneNumber();
		ReflectionTestUtils.setField(phone, "value", input);

		// act and assert
		assertThrows(
			IllegalArgumentException.class,
			() -> formatter.format(phone));
	}

	@Test
	@DisplayName("Unit - Should throw exception when String is blank")
    void formatTest05() {
		// arrange
		String input = "";
		PhoneNumber phone = new PhoneNumber();
		ReflectionTestUtils.setField(phone, "value", input);

		// act and assert
		assertThrows(
			IllegalArgumentException.class,
			() -> formatter.format(phone));
	}

	@Test
	@DisplayName("Unit - Should throw exception when String is null")
    void formatTest06() {
		// arrange
		PhoneNumber phone = new PhoneNumber();

		// act and assert
		assertThrows(
			IllegalArgumentException.class,
			() -> formatter.format(phone));
	}
}