package br.com.ecommerce.cart.api.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class OrderDataDTO {

    private Long userId;
    private List<ProductDTO> products;
    private BigDecimal total;
    private String status;
    private LocalDate createdAt;
}