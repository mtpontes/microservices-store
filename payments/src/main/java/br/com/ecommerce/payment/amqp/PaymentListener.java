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

	@Transactional
	@RabbitListener(queues = "payments.details-order")
	public void receiveQueueMessagesOrder(@Payload PaymentDTO dto) {
		service.createPayment(dto);
	}

	@Transactional
	@RabbitListener(queues = "payments.cancel-order")
	public void receivePaymentsCancelOrder(@Payload String orderId) {
		service.cancelPayment(orderId);
	}
}