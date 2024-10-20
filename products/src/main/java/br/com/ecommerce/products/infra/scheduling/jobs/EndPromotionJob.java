package br.com.ecommerce.products.infra.scheduling.jobs;

import java.time.LocalDateTime;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.business.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class EndPromotionJob implements Job {

    private final ProductService productService;
    private final Scheduler scheduler;


    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("THE END OF THE PROMOTION HAS BEEN TRIGGERED: {}", LocalDateTime.now());  
        Long productId = context.getJobDetail().getJobDataMap().getLong("productId");
        productService.closePromotion(productId);

        try {
            scheduler.deleteJob(context.getJobDetail().getKey());
        } catch (SchedulerException e) {
            log.debug("Unable to delete current job");
            e.printStackTrace();
        }
    }
}