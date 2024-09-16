package br.com.ecommerce.payment.controller;

import java.math.BigDecimal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.payment.model.Payment;
import br.com.ecommerce.payment.model.PaymentConfirmDTO;
import br.com.ecommerce.payment.model.PaymentDTO;
import br.com.ecommerce.payment.model.PaymentStatus;
import br.com.ecommerce.payment.service.PaymentService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	@Autowired
	private PaymentService service;
	@Autowired
	private RabbitTemplate template;


	@GetMapping
	public ResponseEntity<Page<PaymentDTO>> getAll(
		@PageableDefault(size = 10) Pageable pageable,
		@RequestParam(required = false) Long paymentId, 
		@RequestParam(required = false) Long orderId, 
		@RequestParam(required = false) Long userId, 
		@RequestParam(required = false) BigDecimal paymentAmount, 
		@RequestParam(required = false) PaymentStatus status
		) {
		
		Page<PaymentDTO> payments = service.getAllByParams(pageable, paymentId, orderId, userId, paymentAmount, status);
		return ResponseEntity.ok(payments);
	}

	/* 
		* Mock a payment confirmation of a payment provider service
		*/
	@PatchMapping("/{paymentId}")
	@Transactional
	public ResponseEntity<?> confirmPayment(@PathVariable Long paymentId) {
		Payment p = service.confirmPayment(paymentId);
		PaymentConfirmDTO dto = new PaymentConfirmDTO(p.getOrderId(), PaymentStatus.CONFIRMED);
		
		template.convertAndSend("payments.ex", "", dto);
		return ResponseEntity.noContent().build();
	}
}