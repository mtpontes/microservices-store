package br.com.ecommerce.products.utils.factory;

import java.util.List;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.utils.builder.CategoryTestBuilder;

public class CategoryTestFactory {

    private CategoryTestBuilder builder = new CategoryTestBuilder();


    public Category createManufacturer(Long id, String name, Department department) {
        return builder
            .id(id)
            .name(name)
            .department(department)
            .build();
    }

    public Category createManufacturer(Long id, String name, Department department, List<Product> products) {
        return builder
            .id(id)
            .name(name)
            .department(department)
            .products(products)
            .build();
    }
}