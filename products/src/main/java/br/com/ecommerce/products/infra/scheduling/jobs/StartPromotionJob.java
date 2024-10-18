package br.com.ecommerce.products.infra.scheduling.jobs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.business.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StartPromotionJob implements Job {

    private final ProductService productService;


    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("THE START OF THE PROMOTION HAS STARTED: {}", LocalDateTime.now());  
        Long productId = context.getJobDetail().getJobDataMap().getLong("productId");
        String endPromotionString = context.getJobDetail().getJobDataMap().getString("endPromotion");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime endPromotion = LocalDateTime.parse(endPromotionString, formatter);
        productService.startPromotionImediatly(productId, endPromotion);
    }
}