package br.com.ecommerce.products.api.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ResponseError {

    private int status;
    private String error;
    private Object message;
}