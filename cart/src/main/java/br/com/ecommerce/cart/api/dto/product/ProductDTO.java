package br.com.ecommerce.cart.api.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private String id;
    private String name;
    private Integer unit;
    private BigDecimal price;
}