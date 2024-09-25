package br.com.ecommerce.products.infra.entity.category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "Category")
@Table(name = "categories")
public class Category {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false, length = 100)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@OneToMany(mappedBy = "category" , fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<>();

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime modifiedAt;
	
	private boolean isActive;

	public Category(String name, Department department) {
		this.name = this.checkName(name);
		this.department = Optional.ofNullable(department)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: department"));

		this.isActive = true;
	}


	public void update(String name) {
		this.name = this.checkName(name);
	}

	private String checkName(String name) {
		return Optional.ofNullable(name)
			.filter(n -> !n.isBlank())
			.map(n -> n.toUpperCase())
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: name"));
	}

	public void addProduct(Product product) {
		Optional.ofNullable(product)
			.ifPresent(p -> this.products.add(p));
	}

	public void removeProduct(Product product) {
		Optional.ofNullable(product)
			.ifPresent(p -> this.products.remove(p));
	}

	@Override
	public String toString() {
		return "Category(" +
			"id=" + id +
			", name='" + name + '\'' +
			", createdAt=" + createdAt +
			", modifiedAt=" + modifiedAt +
			", isActive=" + isActive +
			')';
	}
}