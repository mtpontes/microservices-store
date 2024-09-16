package br.com.ecommerce.orders.api.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.business.service.OrderService;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

	@Autowired
	private OrderService service;
	@Autowired
	private RabbitTemplate template;


	@GetMapping("/{userId}")
	public ResponseEntity<Page<OrderBasicInfDTO>> getAllBasicsInfoOrdersByUser(
		@PathVariable Long userId,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return ResponseEntity.ok(service.getAllOrdersByUser(pageable, userId));
	}

	@GetMapping("/{orderId}/{userId}")
	public ResponseEntity<OrderDTO> getOrderByIdAndUserId(
		@PathVariable Long orderId,
		@PathVariable Long userId,
		@PageableDefault(size = 10) Pageable pageable
	) {
		return ResponseEntity.ok(service.getOrderById(orderId, userId));
	}

	@PatchMapping("/{orderId}")
	@Transactional
	public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
		service.updateOrderStatus(orderId, OrderStatus.CANCELED);
		
		// cancel payment
		template.convertAndSend("orders-cancel.ex", "cancellation", orderId);
		return ResponseEntity.noContent().build();
	}
}