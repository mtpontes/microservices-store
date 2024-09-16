package br.com.ecommerce.products.infra.entity.tools.factory;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CategoryFactory {

    public Category createCategory(String name, Department department) {
        return new Category(name, department);
    }
}