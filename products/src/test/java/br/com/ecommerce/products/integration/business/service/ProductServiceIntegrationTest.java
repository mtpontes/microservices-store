package br.com.ecommerce.products.integration.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.api.dto.product.CreateProductDTO;
import br.com.ecommerce.products.api.dto.product.DataProductDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.ProductUnitsRequestedDTO;
import br.com.ecommerce.products.api.dto.product.StockWriteOffDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductDTO;
import br.com.ecommerce.products.business.service.ProductService;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.infra.repository.ProductRepository;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;
import br.com.ecommerce.products.utils.util.PriceUtils;
import br.com.ecommerce.products.utils.util.ProductUtils;
import br.com.ecommerce.products.utils.util.StockUtils;
import jakarta.persistence.EntityNotFoundException;

@ServiceIntegrationTest
class ProductServiceIntegrationTest {

    private static List<Product> productsPersisted;

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;


    @BeforeAll
    static void setup(
        @Autowired ProductRepository repository,
        @Autowired PriceUtils priceUtils,
        @Autowired StockUtils stockUtils,
        @Autowired PhoneUtils phoneUtils,
        @Autowired AddressUtils addressUtils,
        @Autowired DepartmentUtils departmentUtils,
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryUtils categoryUtils,
        @Autowired CategoryRepository categoryRepository,
        @Autowired ManufacturerUtils manufacturerUtils,
        @Autowired ManufacturerRepository manufacturerRepository,
        @Autowired ProductUtils productUtils
    ) {
        productsPersisted = IntStream.range(0, 3)
            .mapToObj(flux -> {
                Department department = departmentUtils.getDepartmentInstance();
                department = departmentRepository.save(department);
                
                Category category = categoryUtils.getCategoryInstance(department);
                department.addCategory(category);
                categoryRepository.save(category);
                departmentRepository.save(department);

                Phone phone = phoneUtils.getPhoneInstance();
                Address address = addressUtils.getAddressInstance();
                Manufacturer manufacturer = manufacturerUtils.getManufacturerInstance(phone, address);
                manufacturerRepository.save(manufacturer);
                
                Price price = priceUtils.getPriceInstance();
                Stock stock = stockUtils.getStockInstance();
                Product product = productUtils.getProductInstance(price, stock, category, manufacturer);
                repository.save(product);
                manufacturer.addProduct(product);
                manufacturerRepository.save(manufacturer);
                return product;
            })
            .toList();
    }

    @Test
    @DisplayName("Integration - checkWichProcutsExceedsStock - Must return all products with insuficient stock")
    void checkWichProcutsExceedsStockTest() {
        // assert
        List<ProductUnitsRequestedDTO> stockRequest = List.of(
            new ProductUnitsRequestedDTO(1L, 1001), // exceeds the stock 
            new ProductUnitsRequestedDTO(2L, 1)); // does not exceeds the stock

        // act
        var result = service.checkWichProductsExceedsStock(stockRequest); // returns how many products exceed stock

        // assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Integration - getAllProductsByListOfIds - Getting all products by ID list")
    void getAllProductsByListOfIdsTest01() {
        // act
        var result = service.getAllProductsByListOfIds(List.of(1L, 2L, 3L));

        // assert
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Integration - getAllProductsByListOfIds - Getting all products by list of non-existent IDs")
    void getAllProductsByListOfIdsTest02() {
        // act
        var result = service.getAllProductsByListOfIds(List.of(1000L, 2000L, 3000L));

        // assert
        assertEquals(0, result.size());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateProductData - Should not update all attributes")
    void updateProductDataTest01() {
        // arrange
        Long produtId = productsPersisted.get(0).getId();
        UpdateProductDTO updateData = new UpdateProductDTO(null, null, null);

        // act
        var result = service.updateProductData(produtId, updateData);
        
        // assert
        assertNotEquals(updateData.getName(), result.getName());
        assertNotEquals(updateData.getDescription(), result.getDescription());
        assertNotEquals(updateData.getSpecs(), result.getSpecs());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateProductData - Should update all attributes")
    void updateProductDataTest02() {
        // arrange
        Long ID = productsPersisted.get(0).getId();
        var newName = "update name";
        var newDescription = "update description";
        var newSpecs = "update specs";
        UpdateProductDTO updateData = new UpdateProductDTO(newName, newDescription, newSpecs);

        // act
        var result = service.updateProductData(ID, updateData);
        
        // assert
        assertEquals(newName, result.getName());
        assertEquals(newDescription, result.getDescription());
        assertEquals(newSpecs, result.getSpecs());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateProductData - Should only update the description")
    void updateProductDataTest03() {
        // arrange
        Long ID = 1L;
        var newDescription = "update description";
        UpdateProductDTO updateData = new UpdateProductDTO(null, newDescription, null);

        // act
        var result = service.updateProductData(ID, updateData);
        
        // assert
        assertEquals(newDescription, result.getDescription());
        assertNotNull(result.getName());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateProductData - Should only update the name")
    void updateProductDataTest04() {
        // arrange
        Long ID = 1L;
        var newName = "updated name";
        UpdateProductDTO updateData = new UpdateProductDTO(newName, null, null);

        // act
        var result = service.updateProductData(ID, updateData);
        
        // assert
        assertEquals(newName, result.getName());
        assertNotNull(result.getDescription());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateProductData - Should throw exception when name already exists")
    void updateProductDataTest05() {
        // arrange
        Long ID = 1L;
        String existentName = productsPersisted.get(0).getName();
        UpdateProductDTO updateData = new UpdateProductDTO(
            existentName, null, null);

        // act
        assertThrows(
            IllegalArgumentException.class,
            () -> service.updateProductData(ID, updateData));
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateStockByProductId - Must reduce the units in stock")
    void updateStockByProductIdTest01() {
        // arrange
        DataStockDTO entry = new DataStockDTO(-2);
        
        Product target = productsPersisted.get(0);
        Long TARGET_ID = target.getId();
        String EXPECTED_NAME = target.getName();
        Integer EXPECTED_UNITS = target.getStock().getUnit() + entry.getUnit();

        // act
        var result = service.updateStockByProductId(TARGET_ID, entry);

        // assert
        assertEquals(EXPECTED_UNITS, result.getUnit());
        assertEquals(EXPECTED_NAME, result.getName());
        assertEquals(TARGET_ID, result.getProductId());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateStockByProductId - Must increase the units in stock")
    void updateStockByProductIdTest02() {
        // arrange
        DataStockDTO input = new DataStockDTO(2);
        
        Product target = productsPersisted.get(0);
        Long TARGET_ID = target.getId();
        Integer EXPECTED_UNITS = target.getStock().getUnit() + input.getUnit();

        // act
        var result = service.updateStockByProductId(TARGET_ID, input);

        // assert
        assertEquals(EXPECTED_UNITS, result.getUnit());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateStocks - Should update all stocks")
    void updateStocksTest() {
        // arrange
        Map<Long, Integer> ORIGINAL_STOCKS_UNITS = productsPersisted.stream()
            .collect(Collectors.toMap(Product::getId, p -> p.getStock().getUnit()));

        List<StockWriteOffDTO> input = productsPersisted.stream()
            .map(p -> new StockWriteOffDTO(p.getId(), p.getStock().getUnit())).toList();

        // act
        service.updateStocks(input);

        // assert
        repository.findAll().forEach(p -> {
            Integer ORIGINAL_VALUE = ORIGINAL_STOCKS_UNITS.get(p.getId());
            Integer EXPECTED = ORIGINAL_VALUE - ORIGINAL_VALUE;
            Integer CURRENT = p.getStock().getUnit();

            assertEquals(EXPECTED, CURRENT);
            assertNotEquals(ORIGINAL_VALUE, CURRENT);
        });
    }

    @Rollback
    @Test
    @DisplayName("Integration - createProduct - Should create a Product")
    void createProductTest01() {
        // arrange
        CreateProductDTO input = new CreateProductDTO(
            "name",
            "description",
            "specs",
            productsPersisted.get(0).getCategory().getId(),
            productsPersisted.get(0).getManufacturer().getId()
        );

        // act
        DataProductDTO result = service.createProduct(input);

        // assert
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getDescription(), result.getDescription());
        assertEquals(input.getSpecs(), result.getSpecs());
        assertEquals(input.getCategoryId(), result.getCategory().getId());
        assertEquals(input.getManufacturerId(), result.getManufacturer().getId());
    }

    @Rollback
    @Test
    @DisplayName("Integration - createProduct - Should fail when passing a existent name")
    void createProductTest02() {
        // arrange
        String existentName = productsPersisted.get(0).getName();
        Long unexistentCategoryId = 1000L;
        CreateProductDTO input = new CreateProductDTO(
            existentName,
            "description",
            "specs",
            unexistentCategoryId,
            productsPersisted.get(0).getManufacturer().getId()
        );

        assertThrows(
            IllegalArgumentException.class, 
            () -> service.createProduct(input));
    }

    @Rollback
    @Test
    @DisplayName("Integration - createProduct - Should fail when passing a non-existent Category")
    void createProductTest03() {
        // arrange
        Long unexistentCategoryId = 1000L;
        CreateProductDTO input = new CreateProductDTO(
            "name",
            "description",
            "specs",
            unexistentCategoryId,
            productsPersisted.get(0).getManufacturer().getId()
        );

        assertThrows(
            EntityNotFoundException.class, 
            () -> service.createProduct(input));
    }

    @Rollback
    @Test
    @DisplayName("Integration - createProduct - Should fail when passing a non-existent Manufacturer")
    void createProductTest04() {
        // arrange
        Long unexistentManufacturerId = 1000L;
        CreateProductDTO input = new CreateProductDTO(
            "name",
            "description",
            "specs",
            productsPersisted.get(0).getCategory().getId(),
            unexistentManufacturerId
        );

        assertThrows(
            EntityNotFoundException.class, 
            () -> service.createProduct(input));
    }
}