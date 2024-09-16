package br.com.ecommerce.products.unit.infra.entity.manufacturer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.business.formatter.PhoneFormatter;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.tools.interfaces.Formatter;

class PhoneTest {

    private final Formatter<Phone> phoneFormatter = new PhoneFormatter();


    @Test
    void testCreatePhone_withValidValues() {
        // arrange
        String phoneString = "47 98632-3805";
        String expectedFormatting = "+55 47 98632-3805";

        // act
        Phone phone = new Phone(phoneString, phoneFormatter);

        // assert
        assertEquals(expectedFormatting, phone.getValue());
    }

    @Test
    void testCreatePhone_withNullValues() {
        // arrange
        String phoneString = "47 98632-3805";

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> new Phone(null, phoneFormatter));

        assertThrows(
            IllegalArgumentException.class,
            () -> new Phone(phoneString, null));
    }

    @Test
    void testCreatePhone_withBlankString() {
        // arrange
        String phoneString = "";

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> new Phone(phoneString, phoneFormatter));
    }
}