package br.com.ecommerce.orders.api.mapper;

import org.springframework.stereotype.Component;

import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.infra.entity.Product;

@Component
public class ProductMapper {

    public ProductDTO toProductDTO(Product data) {
        return new ProductDTO(data.getId(), data.getName(), data.getUnit(), data.getPrice());
    }
}