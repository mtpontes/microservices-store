package br.com.ecommerce.products.infra.entity.manufacturer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.ecommerce.products.infra.entity.product.Product;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "Manufacturer")
@Table(name = "manufacturers")
public class Manufacturer {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 100)
	private String name;

	@Embedded
	@AttributeOverride(
		name = "value",
		column = @Column(
			name = "phone",
			length = 19))
	private Phone phone;

	@Column(length = 100)
	private String email;

	@Column(length = 100)
	private String contactPerson;

	@Embedded
	private Address address;

	@OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<>();

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime modifiedAt;
	
	private boolean isActive;

	public Manufacturer(String name, Phone phone, String email, String contactPerson, Address address) {
		this.name = this.notBlank(name, "name").toUpperCase();
		this.phone = this.notNull(phone, "phone");
		this.email = Optional.ofNullable(email).orElse("");
		this.contactPerson = Optional.ofNullable(contactPerson).orElse("");
		this.address = this.notNull(address, "address");
	}

	public void update(String name, Phone phone, String email, String contactPerson, Address address) {
		Optional.ofNullable(name)
			.filter(n -> !n.isBlank())
			.map(n -> n.toUpperCase())
			.ifPresent(n -> this.name = n);

		Optional.ofNullable(phone)
			.ifPresent(p -> this.phone = p);

		Optional.ofNullable(address)
			.ifPresent(a -> this.address = a);

		Optional.ofNullable(email)
			.filter(e -> !e.isBlank())
			.ifPresent(e -> this.email = e);

		Optional.ofNullable(contactPerson)
			.filter(c -> !c.isBlank())
			.ifPresent(c -> this.contactPerson = c);
	}

	public void addProduct(Product product) {
		this.products.add(product);
	}

	public void removeProduct(Product product) {
		this.products.remove(product);
	}

	private String notBlank(String field, String fieldName) {
		return Optional.ofNullable(field)
			.filter(s -> !s.isBlank())
			.orElseThrow(() -> new IllegalArgumentException("Cannot be blank: " + fieldName));
	}

	private <T> T notNull(T attribute, String attributeName) {
		return Optional.ofNullable(attribute)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + attributeName));
	}

	public String getPhone() {
		return this.phone.getValue();
	}

	@Override
	public String toString() {
		return "Manufacturer(" +
				"id=" + id +
				", name='" + name + '\'' +
				", phone=" + phone +
				", email='" + email + '\'' +
				", contactPerson='" + contactPerson + '\'' +
				", address=" + address +
				", createdAt=" + createdAt +
				", modifiedAt=" + modifiedAt +
				", isActive=" + isActive +
				')';
	}
}