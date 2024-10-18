package br.com.ecommerce.products.infra.scheduling.scheduler;

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

import br.com.ecommerce.products.infra.scheduling.jobs.EndPromotionJob;
import br.com.ecommerce.products.infra.scheduling.jobs.StartPromotionJob;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PriceJobScheduler {

    private final Scheduler scheduler;

    
    public void removeRedundantSchedulePromotion(JobKey target) {
        try {
            if (this.scheduler.checkExists(target)) {
                try {
                    scheduler.deleteJob(target);
                } catch (Exception ex) {
                    throw new RuntimeException("An error occurred while deleting job", ex);
                }
            }
        } catch (SchedulerException ex) {
            throw new RuntimeException("An error occurred while checking the existence of a job", ex);
        }
    }

    public void removeRedundantSchedulePromotion(Long productId, PromotionOperation operation) {
        JobKey target = this.createJobKey(productId, operation);
        this.removeRedundantSchedulePromotion(target);
    }

    public void createSchedulerForPromotionStart(Long productId, LocalDateTime start, LocalDateTime end) {
        PromotionOperation promotionOperation = PromotionOperation.START_PROMOTION;
        JobKey target = this.createJobKey(productId, promotionOperation);
        this.removeRedundantSchedulePromotion(target);

        JobDetail jobDetail = JobBuilder.newJob().ofType(StartPromotionJob.class)
            .withIdentity(target.getName(), promotionOperation.groupName)
            .usingJobData("productId", productId)
            .usingJobData("endPromotion", end.toString())
            .storeDurably()
            .build();
            
        Trigger trigger = this.createTrigger(productId, start, promotionOperation);
        this.createJob(jobDetail, trigger);
    }

    public void createSchedulerForPromotionEnd(Long productId, LocalDateTime end) {
        PromotionOperation operation = PromotionOperation.END_PROMOTION;
        JobKey target = this.createJobKey(productId, operation);
        this.removeRedundantSchedulePromotion(target);

        JobDetail jobDetail = JobBuilder.newJob().ofType(EndPromotionJob.class)
            .withIdentity(target.getName(), operation.groupName)
            .usingJobData("productId", productId)
            .storeDurably()
            .build();
        Trigger trigger = this.createTrigger(productId, end, operation);

        this.createJob(jobDetail, trigger);
    }

    private JobKey createJobKey(Long productId, PromotionOperation promotionOperation) {
        return new JobKey(promotionOperation.jobName + "_" + productId, promotionOperation.groupName);
    }

    private Trigger createTrigger(Long productId, LocalDateTime date, PromotionOperation operation) {
        return TriggerBuilder.newTrigger()
            .withIdentity(operation.triggerName + "_" + productId, operation.groupName)
            .startAt(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))
            .build();
    }

    private void createJob(JobDetail jobDetail, Trigger trigger) {
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            
        } catch (SchedulerException ex) {
            throw new RuntimeException("Cannot possible create job", ex);
        }
    }

    public enum PromotionOperation {
        START_PROMOTION(
            "job_startPromotion", 
            "startPromotionGroup", 
            "startPromotionTrigger"),
        END_PROMOTION(
            "job_endPromotion", 
            "endPromotionGroup", 
            "endPromotionTrigger");

        private String jobName;
        private String groupName;
        private String triggerName;

        PromotionOperation(String jobName, String jobGroup, String triggerName) {
            this.jobName = jobName;
            this.groupName = jobGroup;
            this.triggerName = triggerName;
        }
    }
}