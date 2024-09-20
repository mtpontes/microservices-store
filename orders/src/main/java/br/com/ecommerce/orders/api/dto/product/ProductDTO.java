package br.com.ecommerce.orders.api.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @NotNull
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private Integer unit;

    @NotNull
    private BigDecimal price;
}