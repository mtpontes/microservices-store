package br.com.ecommerce.products.integration.business.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.api.dto.category.CreateCategoryDTO;
import br.com.ecommerce.products.api.dto.category.SimpleDataCategoryDTO;
import br.com.ecommerce.products.api.dto.category.UpdateCategoryDTO;
import br.com.ecommerce.products.business.service.CategoryService;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;
import jakarta.persistence.EntityNotFoundException;

@ServiceIntegrationTest
class CategoryServiceIntegrationTest {

    private static List<Category> categoriesPersisted;

    @Autowired
    private CategoryService service;

    @BeforeAll
    static void setup(
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryUtils categoryUtils,
        @Autowired CategoryRepository categoryRepository
    ) {
        categoriesPersisted = IntStream.range(0, 3)
            .mapToObj(flux -> {
                Department department = departmentUtils.getDepartmentInstance();
                department = departmentRepository.save(department);
                
                Category category = categoryUtils.getCategoryInstance(department);
                department.addCategory(category);
                categoryRepository.save(category);
                departmentRepository.save(department);
                return category;
            })
            .toList();
    }


    @Rollback
    @Test
    @DisplayName("Integration - createCategory - Should create a Category")
    void createCategory01() {
        // arrange
        CreateCategoryDTO input = new CreateCategoryDTO(
            categoriesPersisted.get(0).getDepartment().getId(),
            "name"
        );

        // act
        SimpleDataCategoryDTO result = service.create(input);

        // assert
        assertNotNull(result.getId());
        assertEquals(input.getName().toUpperCase(), result.getName());
    }

    @Rollback
    @Test
    @DisplayName("Integration - createCategory - Should throw exception when name already exists")
    void createCategory02() {
        // arrange
        String existentName = categoriesPersisted.get(0).getName();
        CreateCategoryDTO input = new CreateCategoryDTO(null, existentName);

        // act and assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> service.create(input));
    }

    @Rollback
    @Test
    @DisplayName("Integration - createCategory - Should throw exception when not finding Department")
    void createCategory03() {
        // arrange
        Long unexistentManufacturerId = 10000L;
        CreateCategoryDTO input = new CreateCategoryDTO(
            unexistentManufacturerId,
            "name"
        );

        // act and assert
        assertThrows(
            EntityNotFoundException.class, 
            () -> service.create(input));
    }

    @Rollback
    @Test
    @DisplayName("Integration - update - Must update category")
    void updateTest01() {
        // arrange
        Category target = categoriesPersisted.get(0);
        Long targetId = target.getId();
        String targetOriginalName = target.getName();
        String newName = "newName";
        UpdateCategoryDTO requestBody = new UpdateCategoryDTO(newName);

        // act
        SimpleDataCategoryDTO result = service.update(targetId, requestBody);

        // assert
        assertNotEquals(targetOriginalName, result.getName());
        assertEquals(requestBody.getName().toUpperCase(), result.getName());
    }

    @Rollback
    @Test
    @DisplayName("Unit - update - Should throw exception when name already exists")
    void updateDepartmentTest02() {
        // arrange
        String existentName = categoriesPersisted.get(0).getName();

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> service.update(1L, new UpdateCategoryDTO(existentName)));
    }

    @Rollback
    @Test
    @DisplayName("Unit - update - Should throw exception when name is blank")
    void updateDepartmentTest03() {
        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> service.update(1L, new UpdateCategoryDTO()));
    }

    @Rollback
    @Test
    @DisplayName("Unit - update - Should throw exception when not finding manufacturer")
    void updateDepartmentTest04() {
        // act and assert
        Long unexistentManufacturerId = 1000000L;
        assertThrows(
            EntityNotFoundException.class,
            () -> service.update(unexistentManufacturerId, new UpdateCategoryDTO("not blank")));
    }
}