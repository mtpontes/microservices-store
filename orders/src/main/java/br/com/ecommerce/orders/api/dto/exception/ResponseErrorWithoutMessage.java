package br.com.ecommerce.orders.api.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ResponseErrorWithoutMessage {

    private int status;
    private String error;
}