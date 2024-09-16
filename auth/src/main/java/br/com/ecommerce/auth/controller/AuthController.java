package br.com.ecommerce.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.auth.exception.UserNotFoundException;
import br.com.ecommerce.auth.service.TokenService;


@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private TokenService tokenService;
	@Autowired
	private JdbcTemplate jdbcTemplate;


	@GetMapping
	public ResponseEntity<Map<String, Object>> getUserIdAndRoleByToken(@RequestHeader("Authorization") String token) {
		String username = tokenService.validateToken(token);
		Map<String, Object> userData = this.findUser(username);
		return ResponseEntity.ok().body(userData);
	}

	private Map<String, Object> findUser(String username) {
		String query = "SELECT id, username, role FROM users WHERE username = ?";

		Map<String, Object> userData;
		try {
			userData = jdbcTemplate.queryForMap(query, username);

		} catch(DataAccessException ex) {
			ex.printStackTrace();
			throw new UserNotFoundException(ex);
		}
		return userData;
	}
}