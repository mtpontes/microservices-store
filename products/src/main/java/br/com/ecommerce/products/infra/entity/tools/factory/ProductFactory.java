package br.com.ecommerce.products.infra.entity.tools.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Product;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ProductFactory {

    public Product createProduct(
        String name, 
        String description, 
        String specs,
        Category category, 
        Manufacturer manufacturer
    ) {
        return new Product(
            name, 
            description, 
            specs, 
            category, 
            manufacturer
        );
    }
}