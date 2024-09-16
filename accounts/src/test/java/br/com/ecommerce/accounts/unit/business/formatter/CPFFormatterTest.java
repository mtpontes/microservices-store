package br.com.ecommerce.accounts.unit.business.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.business.formatter.CPFFormatter;
import br.com.ecommerce.accounts.model.valueobjects.CPF;

public class CPFFormatterTest {
    
    private CPFFormatter formatter = new CPFFormatter();


    @Test
    @DisplayName("Unit - Must keep current formatting")
    void formatTest01() {
        // arrange
        String input = "961.121.450-22";
        
        CPF cpfFormatted = new CPF();
        ReflectionTestUtils.setField(cpfFormatted, "value", input);

        // act
        var resultFormatted = formatter.format(cpfFormatted);

        // assert
        assertEquals(input, resultFormatted);
    }

    @Test
    @DisplayName("Unit - It should format as expected")
    void formatTest02() {
        // arrange
        String input = "96112145022";
        String expected = "961.121.450-22";
        
        CPF cpfUnformatted = new CPF();
        ReflectionTestUtils.setField(cpfUnformatted, "value", input);

        // act
        var resultUnformatted = formatter.format(cpfUnformatted);

        // assert
        assertEquals(expected, resultUnformatted);
    }

    @Test
    @DisplayName("Unit - Should throw exception when in invalid format")
    void formatTest03() {
        // arrange
        String input = "961-121-450.22";
        
        CPF cpfWithInvalidFormat = new CPF();
        ReflectionTestUtils.setField(cpfWithInvalidFormat, "value", input);

        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> formatter.format(cpfWithInvalidFormat));
    }

    @Test
    @DisplayName("Unit - Should throw exception when CPF is invalid")
    void formatTest04() {
        // arrange
        String input = "961-121-450.23";
        
        CPF cpfWithInvalidFormat = new CPF();
        ReflectionTestUtils.setField(cpfWithInvalidFormat, "value", input);

        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> formatter.format(cpfWithInvalidFormat));
    }
}