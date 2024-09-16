package br.com.ecommerce.products.unit.infra.entity.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.product.Product;

class ProductTest {

    private final String name = "name";
    private final String description = "description";
    private final String specs = "specs";
    private final Category category = new Category();
    private final Manufacturer manufacturer = new Manufacturer();

    private final Product defaultProduct = new Product(name, description, specs, category, manufacturer);


    @Test
    void testCreateProduct_withValidValues() {
        assertDoesNotThrow(() -> new Product(
            name,
            description,
            specs,
            category,
            manufacturer
        ));
    }

    @Test
    void testCreateProduct_withBlankStrings() {
        // blank name is not allowed
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product("", description, specs, category, manufacturer));
        

        // description and specs blank is allowed
        assertDoesNotThrow(
            () -> {
                Product result = new Product(name, "", specs, category, manufacturer);
                assertEquals("", result.getDescription());
            });

        assertDoesNotThrow(
            () -> {
                Product result = new Product(name, description, "", category, manufacturer);
                assertEquals("", result.getSpecs());
            });
    }

    @Test
    void testCreateProduct_withNullParams() {
        // blank name is not allowed
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(null, description, specs, category, manufacturer));
        
        // if the value is null, sets a blank String
        assertDoesNotThrow(
            () -> {
                Product result = new Product(name, null, specs, category, manufacturer);
                assertEquals("", result.getDescription());
            });

        // if the value is null, sets a blank String
        assertDoesNotThrow(
            () -> {
                Product result = new Product(name, description, null, category, manufacturer);
                assertEquals("", result.getSpecs());
            });

        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(null, description, specs, category, manufacturer));
        
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(null, description, specs, null, manufacturer));
        
        // If the value is null, instantiate a Stock with unit 0
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                Product result = new Product(null, description, specs, category, manufacturer);
                assertEquals(Integer.valueOf(0), result.getStock().getUnit());
            });

        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(null, description, specs, category, null));
    }

    @Test
    void testUpdateProduct_withValidValues() {
        // arrange
        final Product product = defaultProduct;

        // act
        String expectedName = "update name";
        String expectedDescription = "update description";
        String expectedSpecs = "update specs";
        product.update(expectedName, expectedDescription, expectedSpecs);

        // assert
        assertEquals(expectedName, product.getName());
        assertEquals(expectedDescription, product.getDescription());
        assertEquals(expectedSpecs, product.getSpecs());
    }

    @Test
    void testUpdateProduct_withBlankStrings() {
        // arrange
        final Product product = defaultProduct;

        // act
        String expectedName = product.getName();
        String expectedDescription = product.getDescription();
        String expectedSpecs = product.getSpecs();
        product.update("", "", "");

        // assert
        assertEquals(expectedName, product.getName());
        assertEquals(expectedDescription, product.getDescription());
        assertEquals(expectedSpecs, product.getSpecs());
    }

    @Test
    void testUpdateProduct_withNullParams() {
        // arrange
        final Product product = defaultProduct;

        // act
        String expectedName = product.getName();
        String expectedDescription = product.getDescription();
        String expectedSpecs = product.getSpecs();
        product.update(null, null, null);

        // assert
        assertEquals(expectedName, product.getName());
        assertEquals(expectedDescription, product.getDescription());
        assertEquals(expectedSpecs, product.getSpecs());
    }
}