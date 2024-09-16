package br.com.ecommerce.products.unit.business.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.business.formatter.PhoneFormatter;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.tools.interfaces.Formatter;

class PhoneFormatterTest {

    private final Formatter<Phone> formatter = new PhoneFormatter();


    @Test
    void testFormatTelefone_comSucesso() {
        // arrange
        String phoneString = "11999999999";
        Phone phone = new Phone();
        ReflectionTestUtils.setField(phone, "value", phoneString);
        
        // act
        String formatedPhone = formatter.format(phone);

        // assert
        assertEquals("+55 11 99999-9999", formatedPhone);
    }
}