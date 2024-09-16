package br.com.ecommerce.orders.mapper;

import org.springframework.stereotype.Component;

import br.com.ecommerce.orders.dto.product.ProductDTO;
import br.com.ecommerce.orders.model.Product;

@Component
public class ProductMapper {

    public ProductDTO toProductDTO(Product data) {
        return new ProductDTO(data.getId(), data.getUnit());
    }
}