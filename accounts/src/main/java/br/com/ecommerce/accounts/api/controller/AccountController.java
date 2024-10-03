package br.com.ecommerce.accounts.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.api.dto.DataUserDTO;
import br.com.ecommerce.accounts.business.service.UserService;
import br.com.ecommerce.common.user.UserDetailsImpl;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {
	
	private final UserService service;
	

	@GetMapping
	public ResponseEntity<DataUserDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl user) {
		return ResponseEntity.ok().body(service.getCurrentUserData(Long.valueOf(user.getId())));
	}
}