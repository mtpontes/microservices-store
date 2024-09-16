package br.com.ecommerce.orders.infra.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {

    AWAITING_PAYMENT("awaiting_payment"),
    CONFIRMED_PAYMENT("confirmed_payment"),
    IN_TRANSIT("in_transit"),
    DELIVERED("delivered"),
    CANCELED("canceled");

    private String status;

    OrderStatus(String status) {
        this.status = status.toUpperCase();
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static OrderStatus fromString(String value) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.status.equalsIgnoreCase(value)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Invalid order status value: " + value);
    }

    public boolean isValidTransition(OrderStatus newStatus) {
		if (this == OrderStatus.AWAITING_PAYMENT) {
			return List.of(OrderStatus.CONFIRMED_PAYMENT, OrderStatus.CANCELED)
				.contains(newStatus);
		}
		if (this == OrderStatus.CONFIRMED_PAYMENT) {
			return List.of(OrderStatus.IN_TRANSIT, OrderStatus.CANCELED)
				.contains(newStatus);
		}
		if (this == OrderStatus.IN_TRANSIT) {
			return List.of(OrderStatus.DELIVERED)
				.contains(newStatus);
		}
		
		if (this == OrderStatus.DELIVERED) {
			return false;
		}
		if (this == OrderStatus.CANCELED) {
			return false;
		}
		return false;
	}
}