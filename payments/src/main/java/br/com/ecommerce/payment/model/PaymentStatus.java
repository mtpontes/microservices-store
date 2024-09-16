package br.com.ecommerce.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {

	AWAITING("awaiting"),
	CONFIRMED("confirmed"),
	CANCELED("canceled");

	private String status;

	PaymentStatus(String status){
		this.status = status.toUpperCase();
	}

	@JsonValue
	public String getStatus() {
		return this.status;
	}

	@JsonCreator
	public static PaymentStatus fromString(String value) {
		for(PaymentStatus paymentStatus : PaymentStatus.values()) {
			if(paymentStatus.status.equalsIgnoreCase(value)) {
				return paymentStatus;
			}
		}
		throw new IllegalArgumentException("Invalid order status value: " + value);
	}
}