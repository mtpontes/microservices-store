package br.com.ecommerce.orders.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecommerce.orders.api.dto.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.infra.entity.Order;

@Component
public class OrderMapper {

    public OrderDTO toOrderDTO(Order data, List<ProductDTO> products) {
        return new OrderDTO(
            data.getId(), 
            data.getUserId(), 
            products, 
            data.getTotal(), 
            data.getDate(), 
            data.getStatus());
    }

    public OrderBasicInfDTO toOrderBasicInfoDTO(Order data) {
        return new OrderBasicInfDTO(
            data.getId(), 
            data.getTotal(), 
            data.getDate(), 
            data.getStatus());
    }
}