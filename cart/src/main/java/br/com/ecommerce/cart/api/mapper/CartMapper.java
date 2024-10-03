package br.com.ecommerce.cart.api.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecommerce.cart.api.dto.cart.CartDTO;
import br.com.ecommerce.cart.api.dto.product.ProductDTO;
import br.com.ecommerce.cart.infra.entity.Cart;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CartMapper {

    public CartDTO toCartDTO(Cart cart, List<ProductDTO> products, BigDecimal total) {
        return new CartDTO(cart.getId(), products, total, cart.isAnon(), cart.getCreatedAt(), cart.getModifiedAt());
    }
}