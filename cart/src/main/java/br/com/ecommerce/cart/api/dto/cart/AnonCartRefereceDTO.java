package br.com.ecommerce.cart.api.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnonCartRefereceDTO {

    @NotBlank
    private String anonCartId;
}