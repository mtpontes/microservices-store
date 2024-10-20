package br.com.ecommerce.products.infra.config;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.business.service.PromotionService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Profile("!test")
@Component
@EnableScheduling
@AllArgsConstructor
public class PromotionsConfig {

    private final PromotionService promotionService;


    /*
     * Remove promotions that expired during system inactivity
     */
    @PostConstruct
    public void removeExpiredPromotions() {
        this.promotionService.removeExpiredPromotions();
        this.promotionService.createScheduleForPromotionsThatWillExpire();
    }

    @PostConstruct
    public void cacheProductsOnSale() {
        this.promotionService.createCacheForProductsOnPromotion();
    }
}