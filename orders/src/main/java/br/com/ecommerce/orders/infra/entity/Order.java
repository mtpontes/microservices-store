package br.com.ecommerce.orders.infra.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity(name = "Order")
@Table(name = "orders")
public class Order {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@OneToMany(
		mappedBy = "order", 
		cascade = CascadeType.ALL, 
		orphanRemoval = true)
	private List<Product> products = new ArrayList<>();

	private BigDecimal total;

	private LocalDate date;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;


	public Order(Long userId, List<Product> products) {
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

	@Override
	public String toString() {
		return String.format(
			"Order[userId=%d, total=%s, status=%s]", 
			this.userId, 
			this.total.toString(), 
			this.status);
	}
}