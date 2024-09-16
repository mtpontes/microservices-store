package br.com.ecommerce.products.infra.config;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.business.service.PriceJobService;
import br.com.ecommerce.products.business.service.PromotionRescanService;
import br.com.ecommerce.products.infra.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@EnableScheduling
@AllArgsConstructor
public class SchedulerConfig {

    private final ProductRepository repository;
    private final PriceJobService jobService;
    private final PromotionRescanService rescanService;


    private void createSchedule() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLate = now.plusHours(1);
        repository.findAllOnPromotionEndingWithinNextHour(now, oneHourLate)
            .forEach(product -> 
                jobService.createScheduleForEndOfPromotion(product.getId(), product.getPrice().getEndOfPromotion()));
    }

    @PostConstruct
    private void runsAfterBoot() {
        this.rescanService.rescanProductsWithExpiredPromotions();
        this.createSchedule();
    }

    @Scheduled(cron = "0 0 * * * *")
    private void runsWithScheduler() {
        this.createSchedule();
    }
}