package br.com.ecommerce.accounts.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "User")
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	@Getter
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Column(length = 100)
	protected String name;

	@Embedded
	protected Login login;

	@Getter
	@Enumerated(EnumType.STRING)
	@Column(updatable = false)
	private UserRole role;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	protected LocalDateTime modifiedAt;

	public User(Login login, String name, UserRole role) {
		this.login = this.notNull(login, "login");
		this.name = this.notBlank(name, "name");
		this.role = this.notNull(role, "role");
	}
	

	protected <T> T notNull(T param, String paramName) {
		return Optional.ofNullable(param)
			.orElseThrow(() -> new IllegalArgumentException("Cannot be null: " + paramName));
	}
	protected String notBlank(String param, String paramName) {
		if (this.notNull(param, paramName).isBlank()) 
			throw new IllegalArgumentException("Cannot be blank: " + paramName);
		return param;
	}

	public String getUsername() {
		return this.login.getUsername();
	}

	public String getPassword() {
		return this.login.getPassword();
	}
}