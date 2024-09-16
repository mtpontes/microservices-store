package br.com.ecommerce.payment.model;

import java.math.BigDecimal;

public record PaymentDTO(
		
	Long orderId,
	Long userId,
	BigDecimal paymentAmount) {

	public PaymentDTO(Payment p) {
		this(p.getOrderId(), p.getUserId(), p.getPaymentAmount());
	}
}