package br.com.ecommerce.cart.api.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartProductDTO {
    
    @NotBlank
    private String id;

    @NotNull
    private Integer unit;
}