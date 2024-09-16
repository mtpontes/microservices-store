package br.com.ecommerce.products.api.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriceDTO {

    @NotNull
    private BigDecimal originalPrice;
    private BigDecimal promotionalPrice;
}