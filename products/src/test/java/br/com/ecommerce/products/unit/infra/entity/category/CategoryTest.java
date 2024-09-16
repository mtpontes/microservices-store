package br.com.ecommerce.products.unit.infra.entity.category;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;

class CategoryTest {

    private final Department department = new Department();


    @Test
    void createCategory_withValidParams() {
        // act
        final String name = "name";
        Category category = new Category(name, department);

        // assert
        assertNotEquals(name, category.getName());
        assertTrue(category.getName().equalsIgnoreCase(name));
    }

    @Test
    void createCategory_withNullAndBlankParam() {
        // act and assert
        final String nullEntry = null;
        assertThrows(
            IllegalArgumentException.class,
            () -> new Category(nullEntry, department));

        final String blankEntry = "";
        assertThrows(
            IllegalArgumentException.class,
            () -> new Category(blankEntry, department));

        assertThrows(
            IllegalArgumentException.class,
            () -> new Category("valid", null));
    }

    @Test
    void testUpdate_withValidParam() {
        // arrange
        final String name = "name";
        final String newName = "updated";
        Category category = new Category(name, department);

        // act
        assertDoesNotThrow(() -> category.update(newName));


        // assert
        assertNotEquals(name, category.getName());
        assertNotEquals(newName, category.getName());
        assertTrue(category.getName().equalsIgnoreCase(newName));
    }

    @Test
    void testUpdate_withNullAndBlankParam() {
        // arrange
        final String name = "name";
        Category categoryNotNull = new Category(name, department);
        Category categoryNotBlank = new Category(name, department);
        
        final String nullUpdate = null;
        final String blankUpdate = "";

        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> categoryNotNull.update(nullUpdate));
        assertThrows(
            IllegalArgumentException.class,
            () -> categoryNotBlank.update(blankUpdate));
    }
}