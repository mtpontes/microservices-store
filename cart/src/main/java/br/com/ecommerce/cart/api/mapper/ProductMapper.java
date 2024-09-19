package br.com.ecommerce.cart.api.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.dto.product.ProductDTO;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.infra.entity.factory.ProductFactory;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProductMapper {

    private final ProductFactory factory;


    public Product toProduct(UpdateCartProductDTO data) {
        return factory.createProduct(data.getId(), data.getUnit());
    }
    
    public ProductDTO toProductDTO(Product data, String name, BigDecimal price) {
        return new ProductDTO(data.getId(), name, data.getUnit(), price);
    }
}