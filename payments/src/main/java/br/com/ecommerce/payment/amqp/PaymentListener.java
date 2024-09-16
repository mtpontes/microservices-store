package br.com.ecommerce.payment.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.ecommerce.payment.model.PaymentDTO;
import br.com.ecommerce.payment.service.PaymentService;
import jakarta.transaction.Transactional;

@Component
public class PaymentListener {

	@Autowired
	private PaymentService service;

	@RabbitListener(queues = "payments.details-order")
	@Transactional
	public void receiveQueueMessagesOrder(@Payload PaymentDTO dto) {
		service.createPayment(dto);
	}

	@RabbitListener(queues = "payments.cancel-order")
	@Transactional
	public void receivePaymentsCancelOrder(@Payload Long orderId) {
		service.cancelPayment(orderId);
	}
}