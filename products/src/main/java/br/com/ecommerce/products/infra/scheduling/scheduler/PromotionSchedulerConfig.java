package br.com.ecommerce.products.infra.scheduling.scheduler;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.business.service.PromotionService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Profile("!test")
@Component
@EnableScheduling
@AllArgsConstructor
public class PromotionSchedulerConfig {

    private final PromotionService promotionService;


    @Scheduled(cron = "0 0 * * * *")
    public void scheduleStarOfPromotions() {
        this.promotionService.createScheduleForPromotionsThatWillStart();
    }

    @PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void scheduleEndOfPromotions() {
        this.promotionService.createScheduleForPromotionsThatWillExpire();
    }
}