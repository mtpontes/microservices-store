package br.com.ecommerce.accounts.model.enums;

public enum UserRole {

	CLIENT("client"),
	EMPLOYEE("employee"),
	ADMIN("admin");
	
	private String role;
	
	UserRole(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return this.role.toUpperCase();
	}
}