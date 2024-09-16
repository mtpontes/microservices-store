package br.com.ecommerce.orders.api.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private Long id;
    private Long userId;
    private List<ProductDTO> products;
    private BigDecimal total;
	@JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private OrderStatus status;
}