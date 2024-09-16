package br.com.ecommerce.products.integration.infra.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.annotations.RepositoryIntegrationTest;
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

@RepositoryIntegrationTest
class ProductRepositoryIntegrationTest {

    private final Pageable pageable = Pageable.unpaged();
    private static List<Product> productsPersisted;
    private static Manufacturer manufacturer;
    private static Category category;
    private static Stock stock;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private PriceUtils priceUtils;
    @Autowired 
    private ProductUtils productUtils;
    @Autowired 
    private ProductRepository productRepository;

    @BeforeAll
    static void setup(
        @Autowired final ProductRepository productRepository,
        @Autowired final PriceUtils priceUtils,
        @Autowired final StockUtils stockUtils,
        @Autowired final PhoneUtils phoneUtils,
        @Autowired final AddressUtils addressUtils,
        @Autowired final DepartmentUtils departmentUtils,
        @Autowired final DepartmentRepository departmentRepository,
        @Autowired final CategoryUtils categoryUtils,
        @Autowired final CategoryRepository categoryRepository,
        @Autowired final ManufacturerUtils manufacturerUtils,
        @Autowired final ManufacturerRepository manufacturerRepository,
        @Autowired final ProductUtils productUtils
    ) {
        productsPersisted = IntStream.range(0, 3)
            .mapToObj(flux -> {
                Department department = departmentUtils.getDepartmentInstance();
                department = departmentRepository.save(department);
                
                category = categoryUtils.getCategoryInstance(department);
                department.addCategory(category);
                categoryRepository.save(category);
                departmentRepository.save(department);

                Phone phone = phoneUtils.getPhoneInstance();
                Address address = addressUtils.getAddressInstance();
                manufacturer = manufacturerUtils.getManufacturerInstance(phone, address);
                manufacturerRepository.save(manufacturer);
                
                Price price = priceUtils.getPriceInstance();
                stock = stockUtils.getStockInstance();
                Product product = productUtils.getProductInstance(price, stock, category, manufacturer);
                productRepository.save(product);
                manufacturer.addProduct(product);
                category.addProduct(product);
                manufacturerRepository.save(manufacturer);
                categoryRepository.save(category);
                return product;
            })
            .collect(Collectors.toList());
    }


    @Test
    @DisplayName("Integration - findAllByParams - Should return all manufacturers when parameters are null")
    void findAllByParamsTest01() {
        // act
        var sizeResult = productRepository.findAllByParams(
            null,
            null,
            null,
            null,
            null,
            pageable
            )
            .getContent()
            .size();

        // asser
        assertEquals(repository.count(), sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - It should return all manufacturers with name like 'name' parameter")
    void findAllByParamsTest02() {
        // arrange
        Product product1 = productUtils.getProductInstance(stock, category, manufacturer);
        ReflectionTestUtils.setField(product1, "name", "a name");
        Product product2 = productUtils.getProductInstance(stock, category, manufacturer);
        ReflectionTestUtils.setField(product2, "name", "a name 2");
        productRepository.saveAll(List.of(product1, product2));

        // act
        String nameParam = "name";
        int sizeResult = productRepository.findAllByParams(
            nameParam,
            null,
            null,
            null,
            null,
            pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return all products with the category equal to the parameter")
    void findAllByParamsTest03() {
        // act
        String categoryName = category.getName();
        int sizeResult = productRepository.findAllByParams(
            null,
            categoryName,
            null,
            null,
            null,
            pageable)
            .getContent()
            .size();

        // assert
        assertEquals(1, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return all products above the minimum price")
    void findAllByParamsTest04() {
        // arrange
        Price price1 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price1, "currentPrice", BigDecimal.valueOf(150));
        Price price2 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price2, "currentPrice", BigDecimal.valueOf(150));
        Price price3 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price3, "currentPrice", BigDecimal.valueOf(200));

        var products = List.of(
            productUtils.getProductInstance(price1, stock, category, manufacturer),
            productUtils.getProductInstance(price2, stock, category, manufacturer),
            productUtils.getProductInstance(price3, stock, category, manufacturer));
        productRepository.saveAll(products);

        // act
        var minPriceParam = BigDecimal.valueOf(150);
        int sizeResult = productRepository.findAllByParams(
            null,
            null,
            minPriceParam,
            null,
            null,
            pageable)
            .getContent()
            .size();

        // assert
        assertEquals(products.size(), sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return all products up to the maximum price")
    void findAllByParamsTest05() {
        // arrange
        Price price1 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price1, "currentPrice", BigDecimal.valueOf(150));
        Price price2 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price2, "currentPrice", BigDecimal.valueOf(200));
        Price price3 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price3, "currentPrice", BigDecimal.valueOf(250));

        var products = List.of(
            productUtils.getProductInstance(price1, stock, category, manufacturer),
            productUtils.getProductInstance(price2, stock, category, manufacturer),
            productUtils.getProductInstance(price3, stock, category, manufacturer));
        productRepository.saveAll(products);

        // act
        var maxPriceParam = BigDecimal.valueOf(100); // PriceUtils sets values ​​to a maximum of 100
        int sizeResult = productRepository.findAllByParams(
            null, null, maxPriceParam, null, null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(productsPersisted.size(), sizeResult);
    }

    @Test
    @DisplayName(
        "Integration - findAllByParams - Must return all products between the minimum price and maximum price")
    void findAllByParamsTest06() {
        // arrange
        Price price1 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price1, "currentPrice", BigDecimal.valueOf(150));
        Price price2 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price2, "currentPrice", BigDecimal.valueOf(200));
        Price price3 = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(price3, "currentPrice", BigDecimal.valueOf(250));

        var products = List.of(
            productUtils.getProductInstance(price1, stock, category, manufacturer),
            productUtils.getProductInstance(price2, stock, category, manufacturer),
            productUtils.getProductInstance(price3, stock, category, manufacturer));
        productRepository.saveAll(products);

        // act
        var minPriceParam = BigDecimal.valueOf(150); 
        var maxPriceParam = BigDecimal.valueOf(250);
        int sizeResult = productRepository.findAllByParams(
            null, null, minPriceParam, maxPriceParam, null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(3, sizeResult);
    }

    @Test
    @DisplayName(
        "Integration - findAllByParams - Must return all products with the manufacturer equal to the parameter")
    void findAllByParamsTest07() {
        // act
        String manufacturerName = manufacturer.getName();
        int sizeResult = productRepository.findAllByParams(
            null, null, null, null, manufacturerName, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(1, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return all products that match all parameters")
    void findAllByParamsTest08() {
        // arrange
        Price priceCustom = priceUtils.getPriceInstance();
        ReflectionTestUtils.setField(priceCustom, "currentPrice", BigDecimal.valueOf(150));
        Product product = productUtils.getProductInstance(priceCustom, stock, category, manufacturer);
        productRepository.save(product);

        // act
        String name = product.getName();
        String categoryName = product.getCategory().getName();
        BigDecimal minPrice = product.getPrice().getCurrentPrice().subtract(BigDecimal.ONE);
        BigDecimal maxPrice = product.getPrice().getCurrentPrice().add(BigDecimal.ONE);
        String manufacturerName = product.getManufacturer().getName();

        int sizeResult = productRepository.findAllByParams(
            name,
            categoryName,
            minPrice,
            maxPrice,
            manufacturerName,
            pageable)
            .getContent()
            .size();

        // assert
        assertEquals(1, sizeResult);
    }
}