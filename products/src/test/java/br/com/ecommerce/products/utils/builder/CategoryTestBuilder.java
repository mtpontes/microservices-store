package br.com.ecommerce.products.utils.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.product.Product;

public class CategoryTestBuilder {

    private Long id;
    private String name;
    private Department department;
    private List<Product> products = new ArrayList<>();


    public CategoryTestBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CategoryTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CategoryTestBuilder department(Department department) {
        this.department = department;
        return this;
    }

    public CategoryTestBuilder products(Product product) {
        this.products.add(product);
        return this;
    }

    public CategoryTestBuilder products(List<Product> products) {
        this.products.addAll(products);
        return this;
    }

    public Category build() {
        Category category = new Category();
        ReflectionTestUtils.setField(category, "id", this.id);
        ReflectionTestUtils.setField(category, "name", this.name);
        ReflectionTestUtils.setField(category, "department", this.department);
        ReflectionTestUtils.setField(category, "products", this.products);

        return category;
    }
}