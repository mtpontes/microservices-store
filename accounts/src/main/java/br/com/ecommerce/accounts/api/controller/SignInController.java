package br.com.ecommerce.accounts.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.api.dto.SignInDTO;
import br.com.ecommerce.accounts.api.dto.TokenDTO;
import br.com.ecommerce.accounts.business.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class SignInController {
	
	private final UserService service;
	

	@PostMapping
	public ResponseEntity<TokenDTO> signIn(@RequestBody @Valid SignInDTO dto) {
		return ResponseEntity.ok(service.signIn(dto));
	}
}