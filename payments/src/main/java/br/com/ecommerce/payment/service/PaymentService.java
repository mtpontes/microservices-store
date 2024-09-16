package br.com.ecommerce.payment.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecommerce.payment.model.Payment;
import br.com.ecommerce.payment.model.PaymentDTO;
import br.com.ecommerce.payment.model.PaymentStatus;
import br.com.ecommerce.payment.repository.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository repository;


	public void createPayment(PaymentDTO dto) {
		Payment payment = new Payment(
			dto.orderId(), 
			dto.userId(), 
			dto.paymentAmount());
		
		repository.save(payment);
	}

	public Payment confirmPayment(Long id) {
		var payment = repository.getReferenceById(id);
		payment.updatePaymentStatus(PaymentStatus.CONFIRMED);
		
		return payment;
	}

	public Payment cancelPayment(Long id) {
		var payment = repository.getReferenceById(id);
		payment.updatePaymentStatus(PaymentStatus.CANCELED);
		
		return payment;
	}

	//admin
	public Page<PaymentDTO> getAllByParams(
			Pageable pageable,
			Long paymentId,
			Long orderId,
			Long userId,
			BigDecimal paymentAmount, 
			PaymentStatus status) {
		
		return repository.findAllByParams(
			pageable,
			paymentId,
			orderId,
			userId,
			paymentAmount,
			status)
			.map(p -> new PaymentDTO(p));
	}
}