package br.com.ecommerce.orders.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAndUnitDTO {

    private Long id;
    private Integer unit;
}