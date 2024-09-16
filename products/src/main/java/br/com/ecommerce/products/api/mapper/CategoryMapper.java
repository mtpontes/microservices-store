package br.com.ecommerce.products.api.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.tools.factory.CategoryFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CategoryMapper {

    private final CategoryFactory factory;


    public Category toCategory(String name, Department department) {
        return factory.createCategory(name, department);
    }

    public SimpleDataCategoryDTO toSimpleDataCategoryDTO(Category data) {
        return Optional.ofNullable(data)
            .map(c -> new SimpleDataCategoryDTO(c.getId(), c.getName()))
            .orElse(null);
    }

    public List<SimpleDataCategoryDTO> toListSimpleDataCategoryDTO(List<Category> data) {
        return Optional.ofNullable(data)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::toSimpleDataCategoryDTO)
            .toList();
    }
}