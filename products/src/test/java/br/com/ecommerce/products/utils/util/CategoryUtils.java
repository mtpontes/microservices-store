package br.com.ecommerce.products.utils.util;

import java.util.List;

import org.springframework.boot.test.context.TestComponent;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.utils.factory.CategoryTestFactory;

@TestComponent
public class CategoryUtils {

    private RandomUtils utils = new RandomUtils();
    private CategoryTestFactory factory = new CategoryTestFactory();


    public Category getCategoryInstance(Department department) {
        return factory.createManufacturer(
            null,
            utils.getRandomString(),
            department
        );
    }

    public Category getCategoryInstanceWithId(
        Department department, 
        List<Product> products
    ) {
        return factory.createManufacturer(
            utils.getRandomLong(),
            utils.getRandomString(),
            department,
            products
        );
    }
}