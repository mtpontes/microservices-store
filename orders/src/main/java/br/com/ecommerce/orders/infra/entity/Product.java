package br.com.ecommerce.orders.infra.entity;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity(name = "Product")
@Table(name = "products")
public class Product {

	private static final BigDecimal MINIMAL_PRICE = BigDecimal.ZERO;
	private static final int MINIMAL_UNIT = 1;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "order_id") 
	private Order order;

	private Long productId;
	private String name;
	private Integer unit;
	private BigDecimal price;

	public Product(Long productId, String name, BigDecimal price, Integer unit) {
		this.productId = this.checkNotNull(productId, "productId");
		this.price = this.checkPrice(price);
		this.unit = this.checkUnit(unit);
	}

	private <T> T checkNotNull(T field, String fieldName) {
		return Optional.ofNullable(field)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + fieldName ));
	}

	private BigDecimal checkPrice(BigDecimal entry) {
		return Optional.ofNullable(entry)
			.filter(p -> p.compareTo(MINIMAL_PRICE) > 0)
			.orElseThrow(() -> new IllegalArgumentException("Price cannot be lass than " + MINIMAL_PRICE));
	}

	private Integer checkUnit(Integer unit) {
		return Optional.ofNullable(unit)
			.filter(units -> units.compareTo(MINIMAL_UNIT) >= 0)
			.orElseThrow(() -> new IllegalArgumentException("Unit cannot be lower than: " + MINIMAL_UNIT));
	}
}