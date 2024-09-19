package br.com.ecommerce.cart.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {
    
    @NotBlank
    private String id;
    
    @NotNull
    private Integer unit;
}