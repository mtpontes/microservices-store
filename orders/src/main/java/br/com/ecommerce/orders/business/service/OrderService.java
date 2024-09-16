package br.com.ecommerce.orders.business.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.api.dto.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.api.http.ProductClient;
import br.com.ecommerce.orders.api.mapper.OrderMapper;
import br.com.ecommerce.orders.api.mapper.ProductMapper;
import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import br.com.ecommerce.orders.infra.entity.Product;
import br.com.ecommerce.orders.infra.exception.OutOfStockException;
import br.com.ecommerce.orders.infra.exception.ProductOutOfStockDTO;
import br.com.ecommerce.orders.infra.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductClient productClient;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private ProductMapper productMapper;


	public Order saveOrder(List<ProductDTO> dtos, Long userId) {
		// validate stock
		this.validateProductsStocks(dtos);
		
		// get products
		return dtos.stream()
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(ProductDTO::getId, ProductDTO::getUnit), 
				mapIdToUnits -> this.getPricedProducts(mapIdToUnits.keySet()).stream()
					.map(p -> new Product(p.getId(), p.getPrice(), mapIdToUnits.get(p.getId())))
					.collect(Collectors.collectingAndThen(
						Collectors.toList(), 
						products -> {
							Order newOrder = new Order(userId, products);
							products.forEach(p -> p.setOrder(newOrder));
							return orderRepository.save(newOrder);
						}))));
	}

	private void validateProductsStocks(List<ProductDTO> dtos) {
		ResponseEntity<List<ProductOutOfStockDTO>> response = this.productClient.verifyStocks(dtos);
		if (response.getStatusCode().equals(HttpStatus.MULTI_STATUS))
			throw new OutOfStockException("There are products out of stock", response.getBody());
		
		if (!response.getStatusCode().equals(HttpStatus.OK)) 
			throw new RuntimeException("Internal server error");
	}
	
	private Set<ProductAndPriceDTO> getPricedProducts(Set<Long> productsId) {
		return Optional.of(productsId)
			.filter(list -> !list.isEmpty())
			.map(list -> productClient.getPrices(productsId))
			.filter(response -> response.getStatusCode().equals(HttpStatus.OK))
			.map(response -> response.getBody())
			.orElseThrow(() -> new IllegalArgumentException("Cannot send an empty list"));
	}

	public OrderDTO getOrderById(Long id, Long userId) {
		return orderRepository.findByIdAndUserId(id, userId)
			.map(order -> order.getProducts().stream()
				.map(productMapper::toProductDTO)
				.collect(Collectors.collectingAndThen(
					Collectors.toList(), 
					productsData -> orderMapper.toOrderDTO(order, productsData)))
			)
			.orElseThrow(EntityNotFoundException::new);
	}

	public Page<OrderBasicInfDTO> getAllOrdersByUser(Pageable pageable, Long userId) {
		return this.orderRepository.findAllByUserId(pageable, userId)
			.map(orderMapper::toOrderBasicInfoDTO);
	}

	public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
		return orderRepository.findById(orderId)
			.map(order -> {
				order.updateOrderStatus(newStatus);
				return orderRepository.save(order);
			})
			.map(order -> order.getProducts().stream()
				.map(productMapper::toProductDTO)
				.collect(Collectors.collectingAndThen(Collectors.toList(), products -> {
					return orderMapper.toOrderDTO(order, products);
				}))
			)
			.orElseThrow(EntityNotFoundException::new);
	}
}