package br.com.ecommerce.orders.infra.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@Document
public class Order {

	@Id
	private String id;
	private String userId;
	private List<Product> products = new ArrayList<>();
	private BigDecimal total;
	private LocalDate date;
	private OrderStatus status;


	public Order(String userId, List<Product> products) {
		this.userId = this.checkNotNull(userId, "userId");
		this.products = Optional.ofNullable(products)
			.filter(p -> !p.isEmpty())
			.orElseThrow(() -> new IllegalArgumentException("Producs list cannot be empty"));

		this.status = OrderStatus.AWAITING_PAYMENT;
		this.total = this.calculateTotalOrderValue();
		this.date = LocalDate.now();
	}

	public void updateOrderStatus(OrderStatus newStatus) {
		if (!this.isValidStatusTransition(newStatus))
			throw new IllegalArgumentException("The status " + this.status + " cannot transition to " + newStatus);
		this.status = newStatus;
	}

	private BigDecimal calculateTotalOrderValue() {
		return this.products.stream()
			.map(p -> p.getPrice().multiply(new BigDecimal(p.getUnit())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private <T> T checkNotNull(T field, String fieldName) {
		return Optional.ofNullable(field)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + fieldName));
	}

	private boolean isValidStatusTransition(OrderStatus newStatus) {
		return this.status.isValidTransition(newStatus);
	}
}