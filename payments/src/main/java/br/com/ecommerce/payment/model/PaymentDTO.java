package br.com.ecommerce.payment.model;

import java.math.BigDecimal;

public record PaymentDTO(
		
	String orderId,
	String userId,
	BigDecimal paymentAmount) {

	public PaymentDTO(Payment p) {
		this(String.valueOf(p.getOrderId()), String.valueOf(p.getUserId()), p.getPaymentAmount());
	}
}