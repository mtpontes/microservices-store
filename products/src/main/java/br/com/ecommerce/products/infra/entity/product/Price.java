package br.com.ecommerce.products.infra.entity.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@Embeddable
public class Price implements Serializable {

    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private BigDecimal promotionalPrice;
    private LocalDateTime startPromotion;
    private LocalDateTime endPromotion;
    private boolean onPromotion;

    public Price() {
        this.onPromotion = false;
    }

    public Price(BigDecimal originalPrice) {
        this.originalPrice = checkPrice(originalPrice);
        this.currentPrice = originalPrice;
        this.onPromotion = false;
    }

    public void setPromotionalPrice(BigDecimal newPromotionalPrice) {
        this.promotionalPrice = this.checkPromotionPrice(newPromotionalPrice);
    }
     
    public void closePromotion() {
        this.currentPrice = this.originalPrice;
        this.endPromotion = null;
        this.startPromotion = null;
        this.onPromotion = false;
    }

    public void initiateAPromotion() {
        this.currentPrice = Optional.ofNullable(this.promotionalPrice)
            .filter(promo -> promo.compareTo(BigDecimal.ZERO) > 0)
            .orElseThrow(() -> new IllegalArgumentException(
                "Unable to start a promotion because the promotional price is null"));
        this.onPromotion = true;
    }

    public void setStartPromotion(LocalDateTime start) {
        if (this.isPastDate(start)) 
            throw new IllegalArgumentException("Invalid start date, please enter a future date");
        if (this.promotionalPrice == null)
            throw new IllegalArgumentException(
                "It is not possible to schedule a promotion while the promotional price of the product is non-existent");
        this.startPromotion = start;
    }
    
    public void setEndPromotion(LocalDateTime end) {
        if (this.isPastDate(end)) throw new IllegalArgumentException("Invalid end date, please enter a future date");
        this.endPromotion = end;
    }

    private BigDecimal checkPrice(BigDecimal price) {
        return Optional.ofNullable(price)
            .filter(original -> original.compareTo(BigDecimal.ZERO) > 0)
            .orElseThrow(() -> new IllegalArgumentException("Price must be a positive value"));
    }

    private BigDecimal checkPromotionPrice(BigDecimal promotional) {
        if (promotional == null) return null; 

        return Optional.of(promotional)
            .filter(promo -> promo.compareTo(BigDecimal.ZERO) > 0)
            .filter(promo -> promo.compareTo(this.originalPrice) < 0)
            .orElseThrow(() -> new IllegalArgumentException(
                "The promotional price must be lower than the original price and must not be equal to zero"));
    }

    private boolean isPastDate(LocalDateTime date) {
        return date.isBefore(LocalDateTime.now());
    }
}