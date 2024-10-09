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
import br.com.ecommerce.accounts.api.openapi.ISignInController;
import br.com.ecommerce.accounts.business.service.TokenProducer;
import br.com.ecommerce.accounts.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class SignInController implements ISignInController {
	
	private final AuthenticationManager authenticationManager;
	private final TokenProducer tokenService;
	

	@PostMapping
	public ResponseEntity<TokenDTO> signIn(@RequestBody @Valid SignInDTO dto) {
		var usernamePasswordToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
		User user = (User) authenticationManager.authenticate(usernamePasswordToken)
			.getPrincipal();
		String jwt = tokenService.generateToken(user);
		return ResponseEntity.ok(new TokenDTO(jwt));
	}
}