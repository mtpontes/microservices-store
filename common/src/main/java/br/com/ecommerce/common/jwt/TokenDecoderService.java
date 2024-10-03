package br.com.ecommerce.common.jwt;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenDecoderService {

	private final String secret;

	public TokenDecoderService(String secret) {
		this.secret = secret;
	}

	
	public DecodedJWT validateToken(String token) {
		token = token.replace("Bearer ", "");
		
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
				.withIssuer("ecommerce")
				.build()
				.verify(token);
				
		} catch (JWTVerificationException exception){
			throw new InvalidTokenException();
		}
	}
}