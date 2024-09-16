package br.com.ecommerce.accounts.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.api.dto.DataUserDTO;
import br.com.ecommerce.accounts.business.service.UserService;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private UserService service;
	

	@GetMapping
	public ResponseEntity<DataUserDTO> getCurrentUser(
		@RequestHeader("X-auth-user-id") Long userId
	) {
		return ResponseEntity.ok()
			.body(service.getCurrentUserData(userId));
	}

	// TODO
	// @PutMapping
	// public ResponseEntity<DataUserDTO> updatePassword(
	// 	@RequestBody @Valid UpdatePasswordDTO dto,
	// 	@RequestHeader("X-auth-user-id") Long userId
	// ) {
	// 	return ResponseEntity.ok().body(service.updatePassword(dto, userId));
	// }
}