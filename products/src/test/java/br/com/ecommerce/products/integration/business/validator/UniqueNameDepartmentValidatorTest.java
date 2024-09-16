package br.com.ecommerce.products.integration.business.validator;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.business.validator.UniqueNameDepartmentValidator;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;

@ServiceIntegrationTest
class UniqueNameDepartmentValidatorTest {

    @Autowired
    private UniqueNameDepartmentValidator validator;
    static private String existentName = "valid name".toUpperCase();


    @BeforeAll
    static void setup(@Autowired DepartmentRepository staticRepository) {
        Department department = new Department(existentName);
        staticRepository.save(department);
    }

    @Test
    void testValidator_withExistentNames() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(existentName));
    }

    @Test
    void testValidator_withNonExistentName() {
        assertDoesNotThrow(() -> validator.validate("random"));
    }
}