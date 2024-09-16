package br.com.ecommerce.products.infra.scheduling;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.business.service.ProductService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PriceJob implements Job {

    private final ProductService productService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long productId = context.getJobDetail().getJobDataMap().getLong("productId");
        productService.switchCurrentPriceToOriginal(productId);
    }
}