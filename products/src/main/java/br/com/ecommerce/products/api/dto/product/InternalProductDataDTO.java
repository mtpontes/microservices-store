package br.com.ecommerce.products.api.dto.product;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternalProductDataDTO implements Serializable {

    private String name;
    private BigDecimal price;
    private String imageLink;
}