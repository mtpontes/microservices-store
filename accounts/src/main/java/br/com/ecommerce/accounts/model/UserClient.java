package br.com.ecommerce.accounts.model;

import java.util.Optional;

import br.com.ecommerce.accounts.model.enums.UserRole;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.model.valueobjects.Login;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Table(name = "user_client")
@Entity(name = "UserClient")
public class UserClient extends User {

	@Embedded
	@AttributeOverride(
		name = "value", 
		column = @Column(
			name = "email", 
			unique = true, 
			nullable = false, 
			length = 50))
	private Email email;

	@Embedded
	@AttributeOverride(
		name = "value", 
		column = @Column(
			name = "phoneNumber", 
			unique = true, 
			nullable = false, 
			length = 19))
	private PhoneNumber phone_number;

	@Embedded
	@AttributeOverride(
		name = "value", 
		column = @Column(
			name = "cpf", 
			unique = true, 
			nullable = false, 
			length = 14))
	private CPF cpf;
	
	@Getter
	@Embedded
	private Address address;

	public UserClient(
		Login login, 
		String name, 
		Email email, 
		PhoneNumber phone_number, 
		CPF cpf, 
		Address address
	) {
		super(login, name, UserRole.CLIENT);

		this.email = this.notNull(email, "email");
		this.phone_number = this.notNull(phone_number, "phoneNumber");
		this.cpf = this.notNull(cpf, "cpf");
		this.address = this.notNull(address, "address");
	}


	public void update(Email newEmail, PhoneNumber newPhone, Address newAddress) {
		Optional.ofNullable(newEmail)
			.ifPresent(e -> this.email = e);
		Optional.ofNullable(newPhone)
			.ifPresent(e -> this.phone_number = e);
		Optional.ofNullable(newAddress)
			.ifPresent(e -> this.address = e);
	}

	public String getEmail() {
		return this.email.getValue();
	}
	public String getPhone_number() {
		return this.phone_number.getValue();
	}
	public String getCPF() {
		return this.cpf.getValue();
	}
}