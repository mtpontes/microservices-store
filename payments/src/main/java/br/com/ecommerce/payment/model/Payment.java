package br.com.ecommerce.payment.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
@Entity(name = "Payment")
@Table(name = "payments")
public class Payment {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long orderId;
	private Long userId;
	private BigDecimal paymentAmount;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;


	public Payment(Long orderId, Long userId, BigDecimal paymentAmount) {
		this.checkNotNull(orderId, "orderId");
		this.checkNotNull(userId, "userId");
		this.checkPaymentAmount(paymentAmount);
		
		this.orderId = orderId;
		this.userId = userId;
		this.paymentAmount = paymentAmount;
		this.status = PaymentStatus.AWAITING;
	}

	private void checkNotNull(Object attribute, String attributeName) {
		if (attribute == null) 
			throw new IllegalArgumentException("Cannot be null: " + attributeName);
	}
	private void checkPaymentAmount(BigDecimal paymentAmount) {
		this.checkNotNull(paymentAmount, "paymentAmount");
		if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Payment amount must be a positive value");
		}
	}

	public void updatePaymentStatus(PaymentStatus newStatus) {
		this.checkNotNull(newStatus, "status");
		if(this.status == PaymentStatus.CANCELED) 
			throw new IllegalArgumentException("Canceled payments cannot be changed");
		
		this.status = newStatus;
	}
}