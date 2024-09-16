package br.com.ecommerce.products.business.service;

import org.springframework.stereotype.Service;

import br.com.ecommerce.products.infra.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PromotionRescanService {

	private final ProductRepository productRepository;


	@Transactional
	public void rescanProductsWithExpiredPromotions() {
		productRepository.findAllWithExpiredPromotions()
			.forEach(product -> product.switchPriceToOriginal());
	}
}