package br.com.ecommerce.orders.business.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ecommerce.orders.api.client.ProductClient;
import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.orders.api.dto.product.ProductAndUnitDTO;
import br.com.ecommerce.orders.api.dto.product.ProductOutOfStockDTO;
import br.com.ecommerce.orders.api.mapper.OrderMapper;
import br.com.ecommerce.orders.api.mapper.ProductMapper;
import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import br.com.ecommerce.orders.infra.entity.Product;
import br.com.ecommerce.orders.infra.exception.exceptions.OrderNotFoundException;
import br.com.ecommerce.orders.infra.exception.exceptions.OutOfStockException;
import br.com.ecommerce.orders.infra.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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


	@Transactional
	public Order saveOrder(Set<ProductAndUnitDTO> dtos, String userId) {
		// validate stock
		Set<ProductOutOfStockDTO> outOfStockProducts = this.productClient.verifyStocks(dtos);
		if (!outOfStockProducts.isEmpty()) throw new OutOfStockException(outOfStockProducts);
		
		// get products
		Set<String> ids = dtos.stream().map(ProductAndUnitDTO::getId).collect(Collectors.toSet());
		Map<String, InternalProductDataDTO> priceMap = this.productClient.getPrices(ids);
		List<Product> products = dtos.stream()
			.map(data -> new Product(
				data.getId(),
				priceMap.get(data.getId()).getName(), 
				priceMap.get(data.getId()).getPrice(), 
				data.getUnit(),
				priceMap.get(data.getId()).getImageLink()))
			.toList();

		Order newOrder = new Order(userId, products);
		return orderRepository.save(newOrder);
	}

	public OrderDTO getOrderById(String id, String userId) {
		return orderRepository.findByIdAndUserId(id, userId)
			.map(order -> order.getProducts().stream()
				.map(productMapper::toProductDTO)
				.collect(Collectors.collectingAndThen(
					Collectors.toList(), 
					productsData -> orderMapper.toOrderDTO(order, productsData)))
			)
			.orElseThrow(OrderNotFoundException::new);
	}

	public Page<OrderBasicInfDTO> getAllOrdersByUser(Pageable pageable, String userId) {
		return this.orderRepository.findAllByUserId(pageable, userId)
			.map(orderMapper::toOrderBasicInfoDTO);
	}

	@Transactional
	public OrderDTO updateOrderStatus(String userId, String orderId, OrderStatus newStatus) {
		return orderRepository.findByIdAndUserId(orderId, userId)
			.map(order -> {
				order.updateOrderStatus(newStatus);
				return orderRepository.save(order);
			})
			.map(order -> order.getProducts().stream()
				.map(productMapper::toProductDTO)
				.collect(Collectors.collectingAndThen(
					Collectors.toList(), 
					products -> orderMapper.toOrderDTO(order, products))))
			.orElseThrow(OrderNotFoundException::new);
	}

	@Transactional
	public OrderDTO updateOrderStatus(String orderId, OrderStatus newStatus) {
		return orderRepository.findById(orderId)
			.map(order -> {
				order.updateOrderStatus(newStatus);
				return orderRepository.save(order);
			})
			.map(order -> order.getProducts().stream()
				.map(productMapper::toProductDTO)
				.collect(Collectors.collectingAndThen(
					Collectors.toList(), 
					products -> orderMapper.toOrderDTO(order, products))))
			.orElseThrow(OrderNotFoundException::new);
	}
}