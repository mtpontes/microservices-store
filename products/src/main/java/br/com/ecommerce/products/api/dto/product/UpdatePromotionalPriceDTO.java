package br.com.ecommerce.products.api.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePromotionalPriceDTO {

    @Min(value = 0, message = "Price cannot be equal to or less than zero")
    private BigDecimal price;
}