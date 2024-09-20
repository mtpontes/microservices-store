package br.com.ecommerce.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.ecommerce.auth.exception.exceptions.InvalidTokenException;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;

	public String validateToken(String token) {
		token = token.replace("Bearer ", "");
		
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
				.withIssuer("ecommerce")
				.build()
				.verify(token)
				.getSubject();
				
		} catch (JWTVerificationException exception){
			throw new InvalidTokenException();
		}
	}
}