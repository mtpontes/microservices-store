package br.com.ecommerce.accounts.business.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import br.com.ecommerce.accounts.infra.exception.FailedCredentialsException;

@Service
public class TokenService {
	
    @Value("${api.security.token.secret}")
    private String secret;

    
    public String generateToken(String subject){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withIssuer("ecommerce")
                .withSubject(subject)
                .withExpiresAt(genExpirationDate())
                .sign(algorithm);
            return token;
            
        } catch (JWTCreationException exception) {
            throw new FailedCredentialsException("Error while generating token", exception);
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now()
            .plusHours(2)
            .toInstant(ZoneOffset.of("-03:00"));
    }
}