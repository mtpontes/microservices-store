package br.com.ecommerce.products.integration.business.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.api.dto.department.CreateDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.SimpleDataDepartmentDTO;
import br.com.ecommerce.products.api.dto.department.UpdateDepartmentoDTO;
import br.com.ecommerce.products.business.service.DepartmentService;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;

@ServiceIntegrationTest
class DepartmentIntegrationTest {

    private static List<Department> departmentsPersisted = new ArrayList<>();

    @Autowired
    private DepartmentService service;

    @BeforeAll
    static void setup(
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryUtils categoryUtils,
        @Autowired CategoryRepository categoryRepository
    ) {
        departmentsPersisted = Stream.generate(() -> {
                Department department = departmentUtils.getDepartmentInstance();
                department = departmentRepository.save(department);
                
                Category category = categoryUtils.getCategoryInstance(department);
                department.addCategory(category);
                
                categoryRepository.save(category);
                departmentRepository.save(department);
                return department;
            })
            .limit(3)
            .toList();
    }


    @Rollback
    @Test
    @DisplayName("Integration - createDepartment - Should create a Deparment")
    void createDepartmentTest01() {
        // arrange
        CreateDepartmentDTO input = new CreateDepartmentDTO("new name");

        // act
        SimpleDataDepartmentDTO result = service.createDepartment(input);

        // assert
        assertEquals(input.getName().toUpperCase(), result.getName());
    }

    @Rollback
    @Test
    @DisplayName("Integration - createDepartment - Should throw exception when name already exists")
    void createDepartmentTest02() {
        // arrange
        String existentName = departmentsPersisted.get(0).getName();
        CreateDepartmentDTO input = new CreateDepartmentDTO(existentName);

        // act
        assertThrows(
            IllegalArgumentException.class,
            () -> service.createDepartment(input));
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateDeparment - Must update Department")
    void updateDeparmentTest01() {
        // arrange
        Department target = departmentsPersisted.get(0);
        Long targetId = target.getId();
        String targetOriginalName = target.getName();
        String newName = "newName";
        UpdateDepartmentoDTO requestBody = new UpdateDepartmentoDTO(newName);

        // act
        SimpleDataDepartmentDTO result = service.updateDepartment(targetId, requestBody);

        // assert
        assertNotEquals(targetOriginalName, result.getName());
        assertEquals(requestBody.getName().toUpperCase(), result.getName());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateDeparment - Must update Department")
    void updateDeparmentTest02() {
        // arrange
        Department target = departmentsPersisted.get(0);
        String existentName = target.getName();
        Long targetId = target.getId();
        UpdateDepartmentoDTO requestBody = new UpdateDepartmentoDTO(existentName);

        // act
        assertThrows(
            IllegalArgumentException.class,
            () -> service.updateDepartment(targetId, requestBody));
    }
}