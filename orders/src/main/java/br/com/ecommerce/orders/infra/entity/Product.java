package br.com.ecommerce.orders.infra.entity;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Product {

	private static final BigDecimal MINIMAL_PRICE = BigDecimal.ZERO;
	private static final int MINIMAL_UNIT = 1;

	private String id;
	private String name;
	private Integer unit;
	private BigDecimal price;
	private String imageLink;

	public Product(String id, String name, BigDecimal price, Integer unit, String image) {
		this.id = this.checkNotNull(id, "product ID");
		this.name = this.checkNotBlank(name, "product name");
		this.price = this.checkPrice(price);
		this.unit = this.checkUnit(unit);
		this.imageLink = image;
	}

	private <T> T checkNotNull(T field, String fieldName) {
		return Optional.ofNullable(field)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + fieldName));
	}

	private String checkNotBlank(String field, String fieldName) {
		return Optional.ofNullable(field)
			.filter(value -> !value.isBlank())
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + fieldName));
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