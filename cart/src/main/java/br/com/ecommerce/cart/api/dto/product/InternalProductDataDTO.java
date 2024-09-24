package br.com.ecommerce.cart.api.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InternalProductDataDTO {

    private String name;
    private BigDecimal price;
    private String imageLink;
}