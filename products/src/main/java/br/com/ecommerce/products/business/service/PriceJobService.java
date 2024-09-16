package br.com.ecommerce.products.business.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.infra.scheduling.PriceJob;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PriceJobService {

    private final Scheduler scheduler;


    public void createScheduleForEndOfPromotion(Long productId, LocalDateTime endTime) {
        String jobName = String.format("job_%d", productId);
        JobKey target = new JobKey(jobName, "promotionJobs");
        if (this.existsJob(target)) this.removeRedundantSchedulePromotion(productId);

        JobDetail jobDetail = JobBuilder.newJob().ofType(PriceJob.class)
            .withIdentity(jobName, "promotionJobs")
            .usingJobData("productId", productId)
            .storeDurably()
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger_" + productId, "promotionTriggers")
            .startAt(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
            .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            
        } catch (SchedulerException ex) {
            throw new RuntimeException("Cannot possible create job", ex);
        }
    }

    public void removeRedundantSchedulePromotion(Long productId) {
        try {
            String jobName = String.format("job_%d", productId);
            JobKey target = new JobKey(jobName, "promotionJobs");
            if (this.existsJob(target)) {
                scheduler.deleteJob(target);
            }
        } catch (SchedulerException ex) {
            throw new RuntimeException("An error occurred while deleting job", ex);
        }
    }

    private boolean existsJob(JobKey jobKey) {
        try {
            return scheduler.checkExists(jobKey);

        } catch (SchedulerException ex) {
            throw new RuntimeException("An error occurred while checking the existence of a job", ex);
        }
    }
}