package br.com.ecommerce.products.business.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.api.mapper.factory.ProductDTOFactory;
import br.com.ecommerce.products.infra.config.CacheName;
import br.com.ecommerce.products.infra.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PromotionService {

	private final ProductRepository productRepository;
	private final ProductDTOFactory productDtoFactory;
	private final PriceJobService jobService;
	private final CacheManager cacheManager;	


	@Transactional
	public void removeExpiredPromotions() {
		productRepository.findAllWithExpiredPromotions(LocalDateTime.now())
			.forEach(product -> product.switchPriceToOriginal());
	}

	public void createSchedulerForPromotionsThatWillExpire() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime oneHourLate = now.plusHours(1);
		productRepository.findAllOnPromotionEndingBetween(now, oneHourLate)
			.forEach(product -> 
				jobService.createScheduleForEndOfPromotion(product.getId(), product.getPrice().getEndOfPromotion()));
    }

	@Async
	@Transactional
	public void createCacheForProductsOnPromotion() {
		Cache productCache = Optional.ofNullable(cacheManager.getCache(CacheName.PRODUCTS))
			.orElseThrow(() -> new RuntimeException("Could not create cache"));

		productRepository.findAllPromotionalProducts(LocalDateTime.now())
			.forEach(product -> {
				var dataToCache = productDtoFactory.createDataProductDTO(product);
				productCache.put(product.getId(), dataToCache);
			});
	}
}