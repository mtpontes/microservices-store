package br.com.ecommerce.cart.api.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseErrorWithouMessage {
    
    private int status;
    private String error;
}