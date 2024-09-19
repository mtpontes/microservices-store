package br.com.ecommerce.cart.unit.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.cart.infra.entity.Product;

public class ProductTest {

    private final String validId = "1";
    private final Integer validUnit = 1;

    @Test
    void testCreateProduct() {
        assertDoesNotThrow(() -> new Product(validId, validUnit));
    }
    
    @Test
    void testCreateProductWithNullAndBlankValues() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(null, validUnit));
        
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product("", validUnit));
            
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(validId, null));
    }

    @Test
    void testCreateProductWithInvalidUnit() {
        Integer invalidUnit = Integer.valueOf(-100);
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(validId, invalidUnit));
    }

    @Test
    void addUnitTest01_withMuchHigherNegative() {
        // arrange
        Integer unit = 1;
        Product target = new Product(validId, unit);
        Integer invalidUnit = Integer.valueOf(-100);
        Integer expected = 0;

        // act
        target.addUnit(invalidUnit);

        // assert
        assertEquals(expected, target.getUnit());
    }

    @Test
    void addUnitTest02_withSum() {
        // arrange
        Integer unit = 1;
        Product target = new Product(validId, unit);
        Integer invalidUnit = Integer.valueOf(100);
        Integer expected = unit + invalidUnit;

        // act
        target.addUnit(invalidUnit);

        // assert
        assertEquals(expected, target.getUnit());
    }
}