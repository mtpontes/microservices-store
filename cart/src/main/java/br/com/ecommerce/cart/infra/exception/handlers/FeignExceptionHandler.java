package br.com.ecommerce.cart.infra.exception.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException.FeignClientException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class FeignExceptionHandler {

    private final ObjectMapper mapper;

    
    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<Map<String, Object>> feignClientException(FeignClientException ex) {
        Map<String, Object> responseBody = this.deserializeClientResponseError(ex.getMessage());

        int statusCode = 500;
        try {
            statusCode = (int) responseBody.get("status");

        } catch (NullPointerException e) {
            ResponseEntity.internalServerError().build();
        }

        return ResponseEntity
            .status(HttpStatus.valueOf(statusCode))
            .body(responseBody);
    }

    private Map<String, Object> deserializeClientResponseError(String exceptionMessage) {
        String clientResponseBody = exceptionMessage
            .split("]:")[1]
            .trim()
            .replace("[", "")
            .replace("]", "");
        log.info("FEIGN ERROR RESPONSE: {}", clientResponseBody);

        Map<String, Object> responseBody = null;
        try {
            responseBody = mapper.readValue(clientResponseBody, new TypeReference<HashMap<String, Object>>(){});

        } catch (IOException e) {
            ResponseEntity.internalServerError().build();
        }

        return responseBody;
    }  
}