package br.com.ecommerce.orders.api.controller;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.api.dto.payment.PaymentDTO;
import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.api.dto.product.StockWriteOffDTO;
import br.com.ecommerce.orders.api.mapper.OrderMapper;
import br.com.ecommerce.orders.api.mapper.ProductMapper;
import br.com.ecommerce.orders.business.service.OrderService;
import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class ClienteOrderController {

	@Autowired
	private OrderService service;
	@Autowired
	private RabbitTemplate template;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private ProductMapper productMapper;


	@PostMapping
	@Transactional
	public ResponseEntity<OrderDTO> createOrder(
		@RequestBody @Valid List<ProductDTO> dto, 
		@RequestHeader("X-auth-user-id") Long userId,
		UriComponentsBuilder uriBuilder
	) {
		Order order = service.saveOrder(dto, userId);
		
		PaymentDTO paymentCreateRabbit = new PaymentDTO(order.getId(), order.getUserId(), order.getTotal());
		List<StockWriteOffDTO> stockUpdateRabbit = order.getProducts().stream()
			.map(o -> new StockWriteOffDTO(o.getProductId(), o.getUnit() * -1))
			.toList();

		List<ProductDTO> productsData = order.getProducts().stream()
			.map(productMapper::toProductDTO)
			.toList();
		OrderDTO responseBody = orderMapper.toOrderDTO(order, productsData);
		var uri = uriBuilder
			.path("/orders/{orderId}")
			.buildAndExpand(responseBody.getId())
			.toUri();

		template.convertAndSend(
			"orders.create.ex", "payment", paymentCreateRabbit);
		template.convertAndSend(
			"orders.create.ex", "stock", stockUpdateRabbit);

		return ResponseEntity.created(uri).body(responseBody);
	}

	@GetMapping
	public ResponseEntity<Page<OrderBasicInfDTO>> getAllBasicsInfoOrdersByUser(
		@RequestHeader("X-auth-user-id") Long userId,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return ResponseEntity.ok(service.getAllOrdersByUser(pageable, userId));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDTO> getOrderByIdAndUserId(
		@PathVariable Long orderId,
		@RequestHeader("X-auth-user-id") Long userId,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return ResponseEntity.ok(service.getOrderById(orderId, userId));
	}

	@Transactional
	@PatchMapping("/{orderId}")
	public ResponseEntity<Void> cancelOrder(
		@PathVariable Long orderId, 
		@RequestHeader("X-auth-user-id") String token
	) {
		service.updateOrderStatus(orderId, OrderStatus.CANCELED);
		
		// cancel payment
		template.convertAndSend("orders.cancel.ex", "cancellation", orderId);
		return ResponseEntity.noContent().build();
	}
}