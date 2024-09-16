package br.com.ecommerce.products.integration.infra.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import br.com.ecommerce.products.annotations.RepositoryIntegrationTest;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;
import br.com.ecommerce.products.utils.util.ProductUtils;

@RepositoryIntegrationTest
class CategoryRepositoryIntegrationTest {

    private final Pageable pageable = Pageable.unpaged();
    private static Department department;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void setup(
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryUtils categoryUtils,
        @Autowired CategoryRepository categoryRepository,
        @Autowired ProductUtils productUtils
    ) {
        department = departmentUtils.getDepartmentInstance();
        departmentRepository.save(department);

        IntStream.range(0, 4)
            .mapToObj(flux -> {
                Category category = categoryUtils.getCategoryInstance(department);
                return categoryRepository.save(category);
            })
            .collect(Collectors.toList());
    }


    @Test
    @DisplayName("Integration - findAllByParams - Should return all manufacturers when parameters are null")
    void findAllByParamsTest01() {
        // act
        var sizeResult = categoryRepository.findAllByParams(null, pageable)
            .getContent()
            .size();
        
        // assert
        assertEquals(categoryRepository.count(), sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - It should return all categories with name like 'name' parameter")
    void findAllByParamsTest02() {
        // arrange
        List<Category> categories = List.of(
            new Category("name", department),
            new Category("name 2", department)
        );
        categoryRepository.saveAll(categories);

        // act
        String nameParam = "name";
        int sizeResult = categoryRepository.findAllByParams(nameParam, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }
}