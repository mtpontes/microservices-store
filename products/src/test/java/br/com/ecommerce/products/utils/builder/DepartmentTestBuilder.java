package br.com.ecommerce.products.utils.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;

public class DepartmentTestBuilder {

    private Long id;
    private String name;
    private List<Category> categories = new ArrayList<>();


    public DepartmentTestBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public DepartmentTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DepartmentTestBuilder categories(Category category) {
        this.categories.add(category);
        return this;
    }

    public DepartmentTestBuilder categories(List<Category> categories) {
        this.categories.addAll(categories);
        return this;
    }

    public Department build() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", this.id);
        ReflectionTestUtils.setField(department, "name", this.name);
        ReflectionTestUtils.setField(department, "categories", this.categories);

        return department;
    }
}