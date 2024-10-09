package br.com.ecommerce.accounts.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.accounts.api.dto.CreateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.DataUserClientDTO;
import br.com.ecommerce.accounts.api.dto.UpdateUserClientDTO;
import br.com.ecommerce.accounts.api.openapi.IClientAccountController;
import br.com.ecommerce.accounts.business.service.UserService;
import br.com.ecommerce.common.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/client/account")
public class ClientAccountController implements IClientAccountController {
	
	private final  UserService service;


	@PostMapping
	public ResponseEntity<DataUserClientDTO> create(@RequestBody @Valid CreateUserClientDTO dto) {
		return ResponseEntity.ok().body(service.saveClientUser(dto));
	}

	@GetMapping
	public ResponseEntity<DataUserClientDTO> getCurrentUserClientData(
		@AuthenticationPrincipal UserDetailsImpl currentUser
	) {
		return ResponseEntity.ok().body(service.getCurrentUserClientData(Long.valueOf(currentUser.getId())));
	}

	@PutMapping
	public ResponseEntity<DataUserClientDTO> updateCurrentClientData(
		@RequestBody UpdateUserClientDTO dto,
		@AuthenticationPrincipal UserDetailsImpl currentUser
	) {
		return ResponseEntity.ok().body(service.updateUserClient(dto, Long.valueOf(currentUser.getId())));
	}
}