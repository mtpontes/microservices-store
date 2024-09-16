package br.com.ecommerce.payment.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.payment.model.Payment;
import br.com.ecommerce.payment.model.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

	@Query("""
			SELECT p FROM Payment p WHERE 
			(:paymentId IS NULL OR p.id = :paymentId)
			AND (:orderId IS NULL OR p.orderId = : orderId)
			AND (:userId IS NULL OR p.userId = : userId)
			AND (:paymentAmount IS NULL OR p.paymentAmount = : paymentAmount)
			AND (:status IS NULL OR p.status = : status)
		""")
	Page<Payment> findAllByParams(
		Pageable pageable,
		Long paymentId,
		Long orderId,
		Long userId,
		BigDecimal paymentAmount,
		PaymentStatus status);
}