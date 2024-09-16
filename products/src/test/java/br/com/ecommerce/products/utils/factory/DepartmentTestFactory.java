package br.com.ecommerce.products.utils.factory;

import java.util.List;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.utils.builder.DepartmentTestBuilder;

public class DepartmentTestFactory {

    private DepartmentTestBuilder builder = new DepartmentTestBuilder();


    public Department createDepartment(Long id, String name) {
        return builder
            .id(id)
            .name(name)
            .build();
    }

    public Department createDepartment(Long id, String name, List<Category> categories) {
        return builder
            .id(id)
            .name(name)
            .categories(categories)
            .build();
    }
}