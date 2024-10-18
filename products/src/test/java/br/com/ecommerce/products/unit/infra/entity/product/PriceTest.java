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
            assertEquals(expectedEndPromotion, price.getEndPromotion());
            assertFalse(price.isOnPromotion());
        });
    }

    @Test
    void testCreatePrice_withValidValues() {
        final BigDecimal positiveOriginalPrice = BigDecimal.valueOf(50);
        final BigDecimal positivePromotionalPrice = BigDecimal.valueOf(25);
        final BigDecimal expectedCurrentPrice = positiveOriginalPrice;

        assertDoesNotThrow(() -> {
            Price price = new Price(positiveOriginalPrice);
            price.setPromotionalPrice(positivePromotionalPrice);
            assertEquals(positiveOriginalPrice, price.getOriginalPrice());
            assertEquals(positivePromotionalPrice, price.getPromotionalPrice());
            assertEquals(expectedCurrentPrice, price.getCurrentPrice());
        });
    }

    @Test
    void testCreatePrice_onlyWithOriginalPriceValid() {
        final BigDecimal positiveOriginalPrice = BigDecimal.valueOf(50);

        assertDoesNotThrow(() -> {
            Price price = new Price(positiveOriginalPrice);
            assertEquals(positiveOriginalPrice, price.getOriginalPrice());
        });
    }

    @Test
    void testCreatePrice_withOriginalPriceEqualsZeroAndLowerThanZero() {
        // arrange
        final BigDecimal nullValue = null;
        final BigDecimal zero = BigDecimal.ZERO;
        final BigDecimal negative = BigDecimal.valueOf(50).negate();

        // assert
        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(nullValue));
        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(zero));
        assertThrows(
            IllegalArgumentException.class,
            () -> new Price(negative));
    }

    @Test
    void testCreatePrice_withPromotionalEqualsOrGreaterThanOriginalPrice() {
        final BigDecimal original = BigDecimal.valueOf(50);
        final BigDecimal promotionalEquals = original;
        final BigDecimal promotionalGreaterThan = BigDecimal.valueOf(50);

        assertThrows(
            IllegalArgumentException.class,
            () -> {
                Price price = new Price(original);
                price.setPromotionalPrice(promotionalEquals);
            });

        assertThrows(
            IllegalArgumentException.class,
            () -> {
                Price price = new Price(original);
                price.setPromotionalPrice(promotionalGreaterThan);
            });
    }

    @Test
    void testSetPromotionalPrice() {
        // arrange
        final Price price = new Price(BigDecimal.TEN);
        final var originalPrice = price.getOriginalPrice();
        
        // act and assert
        final var greaterThanOriginalPrice = originalPrice.multiply(BigDecimal.TEN);

        assertDoesNotThrow(() -> price.setPromotionalPrice(null)); // accepts null
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> price.setPromotionalPrice(BigDecimal.ZERO));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> price.setPromotionalPrice(BigDecimal.TEN.negate()));
        
        assertThrows(
            IllegalArgumentException.class, 
            () -> price.setPromotionalPrice(greaterThanOriginalPrice));
    }

    @Test
    void testSetStartPromotion() {
        // arrange
        final Price price = new Price(BigDecimal.TEN);
        
        // act and assert
        final var pastDate = LocalDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> price.setStartPromotion(pastDate));
        
        // the product does not have a defined promotionalPrice
        final var validDate = LocalDateTime.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () -> price.setStartPromotion(validDate));
    }

    @Test
    void testSetEndPromotion() {
        // arrange
        final Price price = new Price(BigDecimal.TEN);
        
        // act and assert
        final var pastDate = LocalDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> price.setStartPromotion(pastDate));
    }

    @Test
    void testInitiateAPromotion01() {
        // arrange
        final BigDecimal original = BigDecimal.valueOf(50);
        final BigDecimal promotional = original.divide(BigDecimal.valueOf(2));
        final LocalDateTime endOfPromotion = LocalDateTime.now().plusDays(1);
        final Price price = new Price(original);
        price.setPromotionalPrice(promotional);
        
        // act
        price.setEndPromotion(endOfPromotion);
        price.initiateAPromotion();

        // assert
        assertEquals(price.getPromotionalPrice(), price.getCurrentPrice());
        assertEquals(endOfPromotion, price.getEndPromotion());
        assertTrue(price.isOnPromotion());
    }

    @Test
    void testInitiateAPromotion02_withoutAPromotionalPrice() {
        // arrange
        final BigDecimal original = BigDecimal.valueOf(50);
        final LocalDateTime endOfPromotion = LocalDateTime.now().plusDays(1);
        final Price price = new Price(original);
        
        // act
        price.setEndPromotion(endOfPromotion);
        assertThrows(IllegalArgumentException.class, () -> price.initiateAPromotion());
    }

    @Test
    void testClosePromotion() {
        // arrange
        final BigDecimal original = BigDecimal.valueOf(50);
        final BigDecimal promotional = original.divide(BigDecimal.valueOf(2));
        final LocalDateTime endOfPromotion = LocalDateTime.now().plusDays(1);
        final Price price = new Price(original);
        price.setPromotionalPrice(promotional);
        
        // act
        price.setEndPromotion(endOfPromotion);
        price.initiateAPromotion();
        price.closePromotion();

        // assert
        assertEquals(price.getOriginalPrice(), price.getCurrentPrice());
        assertEquals(null, price.getEndPromotion());
        assertFalse(price.isOnPromotion());
    }
}