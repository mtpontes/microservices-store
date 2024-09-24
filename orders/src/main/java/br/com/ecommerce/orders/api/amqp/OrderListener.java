package br.com.ecommerce.orders.api.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.ecommerce.orders.api.dto.order.StatusTransitionDTO;
import br.com.ecommerce.orders.business.service.OrderService;
import br.com.ecommerce.orders.infra.entity.OrderStatus;

@Component
public class OrderListener {

	@Autowired
	private OrderService service;

	
	@RabbitListener(queues = "orders.status-payment")
	public void consumesPaymentConfirmation(@Payload StatusTransitionDTO dto) {
		service.updateOrderStatus(dto.getOrderId(), OrderStatus.CONFIRMED_PAYMENT);
	}
	
//	/* 
//	 * Mock a logistics mock service
//	 */
//	@RabbitListener(queues = "orders.status-logistic")
//	@Transactional
//	public void recebeFilaMsgLogisticaMock(@Payload StatusTransitionDTO dto) {
//		service.updateOrderStatus(dto.orderId(), dto.status());
//	}
}