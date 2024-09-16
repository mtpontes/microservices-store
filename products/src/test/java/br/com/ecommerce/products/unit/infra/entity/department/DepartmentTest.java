package br.com.ecommerce.products.unit.infra.entity.department;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.infra.entity.department.Department;

class DepartmentTest {

    @Test
    void createDepartment_withValidParams() {
        // act
        final String name = "name";
        Department department = new Department(name);

        // assert
        assertNotEquals(name, department.getName());
        assertTrue(department.getName().equalsIgnoreCase(name));
    }

    @Test
    void createDepartment_withNullAndBlankParam() {
        // act and assert
        final String nullEntry = null;
        assertThrows(
            IllegalArgumentException.class,
            () -> new Department(nullEntry));

        final String blankEntry = "";
        assertThrows(
            IllegalArgumentException.class,
            () -> new Department(blankEntry));
    }

    @Test
    void testUpdate_withValidParam() {
        // arrange
        final String name = "name";
        final String newName = "updated";
        Department department = new Department(name);

        // act
        assertDoesNotThrow(() -> department.update(newName));


        // assert
        assertNotEquals(name, department.getName());
        assertNotEquals(newName, department.getName());
        assertTrue(department.getName().equalsIgnoreCase(newName));
    }

    @Test
    void testUpdate_withNullAndBlankParam() {
        // arrange
        final String name = "name";
        Department departmentNotNull = new Department(name);
        Department departmentNotBlank = new Department(name);
        
        final String nullUpdate = null;
        final String blankUpdate = "";

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> departmentNotNull.update(nullUpdate));
        assertThrows(
            IllegalArgumentException.class,
            () -> departmentNotBlank.update(blankUpdate));
    }
}