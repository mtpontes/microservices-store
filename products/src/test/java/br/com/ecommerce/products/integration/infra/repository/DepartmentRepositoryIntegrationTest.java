package br.com.ecommerce.products.integration.infra.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import br.com.ecommerce.products.annotations.RepositoryIntegrationTest;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.utils.util.DepartmentUtils;

@RepositoryIntegrationTest
class DepartmentRepositoryIntegrationTest {

    private static List<Department> departmentsPersisted = new ArrayList<>();
    private final Pageable pageable = Pageable.unpaged();

    @Autowired
    private DepartmentRepository repository;

    @BeforeAll
    static void setup(
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository
    ) {
        departmentsPersisted = IntStream.range(0, 4)
            .mapToObj(flux -> {
                return departmentRepository.save(departmentUtils.getDepartmentInstance());
            })
            .collect(Collectors.toList());
    }


    @Test
    @DisplayName("Integration - findAllByParams - Should return all departments when parameters are null")
    void findAllByParamsTest01() {
        // act
        var sizeResult = repository.findAllByParams(null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(repository.count(), sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - It should return all departments with name like 'name' parameter")
    void findAllByParamsTest02() {
        // arrange
        Department department1 = new Department("name");
        Department department2 = new Department("name 2");
        departmentsPersisted.add(department1);
        departmentsPersisted.add(department2);
        repository.saveAll(departmentsPersisted);

        // act
        String nameParam = "name";
        int sizeResult = repository.findAllByParams(nameParam, pageable)
            .getContent()
            .size();
        
        // assert
        assertEquals(2, sizeResult);
    }
}