package br.com.ecommerce.orders.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ecommerce.orders.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBasicInfDTO {

    private Long id;
    private BigDecimal totalOrder;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    private OrderStatus status;
}