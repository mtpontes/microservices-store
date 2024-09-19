package br.com.ecommerce.cart.api.dto.cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.ecommerce.cart.api.dto.product.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private String id;
    private List<ProductDTO> products;
    private BigDecimal totalPrice;
    private boolean isAnon;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}