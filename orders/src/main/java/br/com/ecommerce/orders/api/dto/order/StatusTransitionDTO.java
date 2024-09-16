package br.com.ecommerce.orders.api.dto.order;

import br.com.ecommerce.orders.infra.entity.OrderStatus;
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