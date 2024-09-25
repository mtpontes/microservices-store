package br.com.ecommerce.products.infra.entity.department;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.ecommerce.products.infra.entity.category.Category;
import jakarta.persistence.Column;
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
@Entity(name = "Department")
@Table(name = "departments")
public class Department {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@OneToMany(
		mappedBy = "department", 
		fetch = FetchType.LAZY)
	private List<Category> categories;

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime modifiedAt;

	private boolean isActive;

	public Department(String name) {
		this.name = this.checkName(name);
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

	public void addCategory(Category category) {
		Optional.ofNullable(category)
			.ifPresent(p -> this.categories.add(p));
	}

	public void removeProduct(Category category) {
		Optional.ofNullable(category)
			.ifPresent(p -> this.categories.remove(p));
	}

	@Override
	public String toString() {
		return "Department(" +
				"id=" + id +
				", name='" + name + '\'' +
				", createdAt=" + createdAt +
				", modifiedAt=" + modifiedAt +
				", isActive=" + isActive +
				')';
	}
}