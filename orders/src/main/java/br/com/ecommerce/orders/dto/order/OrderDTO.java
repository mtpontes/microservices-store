package br.com.ecommerce.orders.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ecommerce.orders.dto.product.ProductDTO;
import br.com.ecommerce.orders.model.OrderStatus;
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