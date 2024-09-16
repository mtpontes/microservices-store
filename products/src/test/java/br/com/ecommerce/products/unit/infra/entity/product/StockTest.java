package br.com.ecommerce.products.unit.infra.entity.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.infra.entity.product.Stock;

class StockTest {

    @Test
    void testCreateStock_withValidValues() {
        final Integer positive = 50;
        assertDoesNotThrow(() -> {
            Stock stock = new Stock(positive);
            assertEquals(positive, stock.getUnit());
        });

        final Integer zero = 0;
        assertDoesNotThrow(() -> {
            Stock stock = new Stock(zero);
            assertEquals(zero, stock.getUnit());
        });
    }

    @Test
    void testCreateStock_withInvalidValue() {
        final Integer nullValue = null;
        assertThrows(
            IllegalArgumentException.class,
            () -> new Stock(nullValue));

        final Integer negative = -10;
        assertThrows(
            IllegalArgumentException.class,
            () -> new Stock(negative));
    }

    @Test
    void testUpdateStock_withNull() {
        final Integer entry = 100;
        final Stock stock = new Stock(entry);

        final Integer nullValue = null;

        assertDoesNotThrow(() -> {
            final Integer expected = entry;
            stock.update(nullValue);
            assertEquals(expected, stock.getUnit());
        });
    }

    @Test
    void testUpdateStock_withZeroValue() {
        // arrange
        final Integer build = 100;
        final Stock stock = new Stock(build);
        final Integer entry = 0;

        // act and assert
        assertDoesNotThrow(() -> {
            final Integer expected = entry + build;
            stock.update(entry);
            assertEquals(expected, stock.getUnit());
        });
    }

    @Test
    void testUpdateStock_withPositiveValue() {
        // arrange
        final Integer build = 100;
        final Stock stock = new Stock(build);
        final Integer entry = 100;

        // act and assert
        assertDoesNotThrow(() -> {
            final Integer expected = entry + build;
            stock.update(entry);
            assertEquals(expected, stock.getUnit());
        });
    }

    @Test
    void testUpdateStock_withNegativeValue() {
        // arrange
        final Integer build = 100;
        final Stock stock = new Stock(build);
        final Integer entry = -150;

        // act and assert
        assertDoesNotThrow(() -> {
            final Integer expected = 0;
            stock.update(entry);
            assertEquals(expected, stock.getUnit());
        });
    }
}