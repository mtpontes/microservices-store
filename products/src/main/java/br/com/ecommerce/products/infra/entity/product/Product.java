package br.com.ecommerce.products.infra.entity.product;

import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "Product")
@Table(name = "products")
public class Product {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 100)
	private String name;

	@Column(length = 255)
	private String description;

	@Column(length = 255)
	private String specs;

	@Embedded
	private Price price;

	@Embedded
	private Images images;

	@Embedded
	private Stock stock;

	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manufacturer_id")
	private Manufacturer manufacturer;

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime modifiedAt;

	private boolean isActive;

	public Product(String name, String description, String specs, Category category, Manufacturer manufacturer) {
		this.name = Optional.ofNullable(name)
			.filter(n -> !n.isBlank())
			.map(n -> this.notNull(n, "name"))
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: name"));

		this.description = Optional.ofNullable(description).orElse("");
		this.specs = Optional.ofNullable(specs).orElse("");
		this.price = new Price();

		this.category = this.notNull(category, "category");
		this.manufacturer = this.notNull(manufacturer, "manufacturer");
		this.stock = new Stock();
		this.isActive = true;
	}

	public void update(String name, String description, String specs) {
		Optional.ofNullable(name)
			.filter(n -> !n.isBlank())
			.ifPresent(n -> this.name = n);

		Optional.ofNullable(description)
			.filter(d -> !d.isBlank())
			.ifPresent(d -> this.description = d);

		Optional.ofNullable(specs)
			.filter(s -> !s.isBlank())
			.ifPresent(s -> this.specs = s);
	}

	public void updateStock(int value) {
		this.stock.update(value);
	}

	public void updatePrice(Price price) {
		this.price = price;
	}

	public void switchPriceToOriginal() {
		this.price.currentToOriginal();
	}

	public void switchPriceToPromotional(LocalDateTime endOfPromotion) {
		this.price.currentToPromotional(endOfPromotion);
	}

	private <T> T notNull(T attribute, String attributeName) {
		return Optional.ofNullable(attribute)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + attributeName));
	}
}