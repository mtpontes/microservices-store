package br.com.ecommerce.products.unit.infra.entity.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.infra.entity.product.Price;

class PriceTest {

    @Test
    void testCreatePrice_withValidValue() {
        final BigDecimal positiveOriginalPrice = BigDecimal.valueOf(50);
        final BigDecimal expectedCurrentPrice = positiveOriginalPrice;
        final LocalDateTime expectedEndPromotion = null;

        assertDoesNotThrow(() -> {
            Price price = new Price(positiveOriginalPrice);
            assertEquals(positiveOriginalPrice, price.getOriginalPrice());
            assertEquals(expectedCurrentPrice, price.getCurrentPrice());
            assertEquals(expectedEndPromotion, price.getEndOfPromotion());
            assertFalse(price.isOnPromotion());
        });
    }

    @Test
    void testCreatePrice_withValidValues() {
        final BigDecimal positiveOriginalPrice = BigDecimal.valueOf(50);
        final BigDecimal positivePromotionalPrice = BigDecimal.valueOf(25);
        final BigDecimal expectedCurrentPrice = positiveOriginalPrice;

        assertDoesNotThrow(() -> {
            Price price = new Price(positiveOriginalPrice, positivePromotionalPrice);
            assertEquals(positiveOriginalPrice, price.getOriginalPrice());
            assertEquals(positivePromotionalPrice, price.getPromotionalPrice());
            assertEquals(expectedCurrentPrice, price.getCurrentPrice());
        });
    }

    @Test
    void testCreatePrice_onlyWithOriginalPriceValid() {
        final BigDecimal positiveOriginalPrice = BigDecimal.valueOf(50);

        assertDoesNotThrow(() -> {
            Price price = new Price(positiveOriginalPrice, null);
            assertEquals(positiveOriginalPrice, price.getOriginalPrice());
        });
    }

    @Test
    void testCreatePrice_onlyWithOriginalPriceNull() {
        final BigDecimal nullValue = BigDecimal.valueOf(0);
        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(nullValue, null));
    }

    @Test
    void testCreatePrice_withOriginalPriceNegative() {
        final BigDecimal negative = BigDecimal.valueOf(-50);
        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(negative, null));

        final BigDecimal zero = BigDecimal.valueOf(0);
        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(zero, null));
    }

    @Test
    void testCreatePrice_withPromotionalEqualsOrGreaterThanOriginalPrice() {
        final BigDecimal original = BigDecimal.valueOf(50);
        final BigDecimal promotionalEquals = original;
        final BigDecimal promotionalGreaterThan = BigDecimal.valueOf(50);

        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(original, promotionalEquals));

        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(original, promotionalGreaterThan));
    }

    @Test
    void testSwitchCurrentPrice() {
        final BigDecimal original = BigDecimal.valueOf(50);
        final BigDecimal promotional = original.divide(BigDecimal.valueOf(2));
        final Price price = new Price(original, promotional);
        final LocalDateTime endOfPromotion = LocalDateTime.now().plusDays(1);
        
        price.currentToPromotional(endOfPromotion);
        assertEquals(price.getPromotionalPrice(), price.getCurrentPrice());
        assertEquals(endOfPromotion, price.getEndOfPromotion());
        assertTrue(price.isOnPromotion());
        
        price.currentToOriginal();
        assertEquals(price.getOriginalPrice(), price.getCurrentPrice());
        assertEquals(null, price.getEndOfPromotion());
        assertFalse(price.isOnPromotion());
    }
}