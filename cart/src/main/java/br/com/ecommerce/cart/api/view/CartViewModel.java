package br.com.ecommerce.cart.api.view;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.ecommerce.cart.api.client.ProductClient;
import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.cart.api.dto.product.ProductDTO;
import br.com.ecommerce.cart.api.mapper.CartMapper;
import br.com.ecommerce.cart.api.mapper.ProductMapper;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CartViewModel {

	private final ProductClient productClient;
	private final ProductMapper productMapper;
	private final CartMapper cartMapper;


	public CartDTO getCartData(Cart cart) {
		List<ProductDTO> products = this.getSetProductDTO(cart.getProducts());
		return cartMapper.toCartDTO(cart, products, this.calculateTotal(products));
	}

	private List<ProductDTO> getSetProductDTO(Set<Product> products) {
		if (products.isEmpty()) return Collections.emptyList();

		Set<String> productIds = products.stream().map(Product::getId).collect(Collectors.toSet());
		Map<String, InternalProductDataDTO> productMap = this.productClient.getPrices(productIds);
		log.debug("CART VIEW MODEL: {}", productMap);

		return products.stream()
			.map(product -> productMapper.toProductDTO(
				product, 
				productMap.get(product.getId()).getName(),	
				productMap.get(product.getId()).getPrice(),
				productMap.get(product.getId()).getImageLink()))
			.toList();
	}

	private BigDecimal calculateTotal(List<ProductDTO> products) {
		return products.stream()
			.map(product -> product.getPrice().multiply(new BigDecimal(product.getUnit())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}