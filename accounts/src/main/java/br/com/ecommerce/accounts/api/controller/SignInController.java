package br.com.ecommerce.accounts.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.api.dto.SignInDTO;
import br.com.ecommerce.accounts.api.dto.TokenDTO;
import br.com.ecommerce.accounts.business.service.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class SignInController {
	
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	

	@PostMapping
	public ResponseEntity<TokenDTO> signIn(@RequestBody @Valid SignInDTO dto) {
		var usernamePasswordToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
		authenticationManager.authenticate(usernamePasswordToken);
		String jwt = tokenService.generateToken(dto.getUsername());
		return ResponseEntity.ok(new TokenDTO(jwt));
	}
}