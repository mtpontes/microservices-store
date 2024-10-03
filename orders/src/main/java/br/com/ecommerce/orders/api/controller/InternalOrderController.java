package br.com.ecommerce.orders.api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.orders.api.dto.payment.PaymentDTO;
import br.com.ecommerce.orders.api.dto.product.ProductAndUnitDTO;
import br.com.ecommerce.orders.api.dto.product.StockWriteOffDTO;
import br.com.ecommerce.orders.business.service.OrderService;
import br.com.ecommerce.orders.infra.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/internal/orders")
public class InternalOrderController {

	@Autowired
	private OrderService service;
	@Autowired
	private RabbitTemplate template;


	@PostMapping
	public ResponseEntity<Order> createOrder(
		@RequestHeader("X-auth-user-id") String userId,
		@RequestBody @Valid @NotEmpty(message = "Product list is empty") Set<ProductAndUnitDTO> data, 
		UriComponentsBuilder uriBuilder
	) {
		Order order = service.saveOrder(data, userId);
		
		PaymentDTO paymentCreateRabbit = new PaymentDTO(order.getId(), order.getUserId(), order.getTotal());
		List<StockWriteOffDTO> stockUpdateRabbit = order.getProducts().stream()
			.map(o -> new StockWriteOffDTO(o.getId(), Math.negateExact(o.getUnit())))
			.toList();

		var uri = uriBuilder
			.path("/client/orders/{orderId}")
			.buildAndExpand(order.getId())
			.toUri();

		template.convertAndSend("orders.create.ex", "payment", paymentCreateRabbit);
		template.convertAndSend("orders.create.ex", "stock", stockUpdateRabbit);
		return ResponseEntity.created(uri).body(order);
	}
}