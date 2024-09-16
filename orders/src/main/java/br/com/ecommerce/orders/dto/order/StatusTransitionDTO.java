package br.com.ecommerce.orders.dto.order;

import br.com.ecommerce.orders.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatusTransitionDTO {

    private Long orderId;
    private OrderStatus status;
}