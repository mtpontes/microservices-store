package br.com.ecommerce.products.infra.entity.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Embeddable
public class Price {

    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private BigDecimal promotionalPrice;
    private LocalDateTime endOfPromotion;
    private boolean onPromotion;

    public Price() {
        this.onPromotion = false;
    }

    public Price(BigDecimal originalPrice) {
        this.originalPrice = checkPrice(originalPrice);
        this.currentPrice = originalPrice;
        this.onPromotion = false;
    }

    public Price(BigDecimal originalPrice, BigDecimal promotionalPrice) {
        this.originalPrice = checkPrice(originalPrice);
        this.promotionalPrice = checkPromotionPrice(promotionalPrice);
        this.currentPrice = originalPrice;
        this.onPromotion = false;
    }

     
    public void currentToOriginal() {
        this.currentPrice = this.originalPrice;
        this.endOfPromotion = null;
        this.onPromotion = false;
    }
    
    public void currentToPromotional(LocalDateTime endPromotion) {
        this.endOfPromotion = Optional.ofNullable(endPromotion)
            .filter(date -> date.isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new IllegalArgumentException("Enter a valid end date for the promotion"));

        this.currentPrice = Optional.ofNullable(this.promotionalPrice)
            .filter(promo -> promo.compareTo(BigDecimal.ZERO) > 0)
            .orElseThrow(() -> new IllegalArgumentException("It is not possible to change the current price as the " + 
                "promotional price is null"));
        this.onPromotion = true;
    }

    private BigDecimal checkPrice(BigDecimal price) {
        return Optional.ofNullable(price)
            .filter(original -> original.compareTo(BigDecimal.ZERO) > 0)
            .orElseThrow(() -> new IllegalArgumentException(
                "Price must be a positive value"));
    }

    private BigDecimal checkPromotionPrice(BigDecimal promotional) {
        if (promotional == null) return null; 

        return Optional.of(promotional)
            .filter(promo -> promo.compareTo(this.originalPrice) < 0)
            .filter(promo -> promo.compareTo(BigDecimal.ZERO) > 0)
            .orElseThrow(() -> new IllegalArgumentException(
                "Promotional price must be a lower than original Price"));
    }
}