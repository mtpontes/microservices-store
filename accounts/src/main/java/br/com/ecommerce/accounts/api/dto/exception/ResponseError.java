package br.com.ecommerce.accounts.api.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ResponseError {

    private int status;
    private String error;
    private Object message; // can be either a String or a more complex object
}