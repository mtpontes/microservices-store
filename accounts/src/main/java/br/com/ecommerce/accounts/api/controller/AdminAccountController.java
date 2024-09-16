package br.com.ecommerce.accounts.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.api.dto.CreateUserEmployeeDTO;
import br.com.ecommerce.accounts.api.dto.UserEmployeeCreatedDTO;
import br.com.ecommerce.accounts.business.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {
	
	@Autowired
	private UserService service;
	

	@PostMapping
	public ResponseEntity<UserEmployeeCreatedDTO> createAdminUser(@RequestBody @Valid CreateUserEmployeeDTO dto) {
		UserEmployeeCreatedDTO userData = service.saveEmployeeUser(dto);
		return ResponseEntity.ok().body(userData);
	}

	@PostMapping("/employee")
	public ResponseEntity<UserEmployeeCreatedDTO> createEmployeeUser(@RequestBody @Valid CreateUserEmployeeDTO dto) {
		UserEmployeeCreatedDTO userData = service.saveEmployeeUser(dto);
		return ResponseEntity.ok().body(userData);
	}
}