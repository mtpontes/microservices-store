package br.com.ecommerce.products.integration.api.controller.product;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.ecommerce.products.annotations.ControllerIntegrationTest;
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

@ControllerIntegrationTest
class ProductControllerIntegrationTest {

    private final String basePath = "/products";
    private static List<Product> productsPersisted;
    private static Manufacturer manufacturer;
    private static Category category;
    private static Stock stock;

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PriceUtils priceUtils;
    @Autowired
    private ProductUtils productUtils;

    @BeforeAll
    static void setup(
        @Autowired ProductRepository productRepository,
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
    void getProductTest01_withoutParams() throws Exception {
        // arrange
        String path = basePath + "/" + productsPersisted.get(0).getId();

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.description").exists())
            .andExpect(jsonPath("$.specs").exists())
            .andExpect(jsonPath("$.price.originalPrice").exists())
            .andExpect(jsonPath("$.price.currentPrice").exists())
            .andExpect(jsonPath("$.stock.unit").exists())
            .andExpect(jsonPath("$.category.id").exists())
            .andExpect(jsonPath("$.category.name").exists())
            .andExpect(jsonPath("$.manufacturer.id").exists())
            .andExpect(jsonPath("$.manufacturer.name").exists());
    }

    @Test
    void getAllTest01_withoutParams() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(productsPersisted.size())))
            .andExpect(jsonPath("$.content[0].name").exists())
            .andExpect(jsonPath("$.content[0].description").exists())
            .andExpect(jsonPath("$.content[0].specs").exists())
            .andExpect(jsonPath("$.content[0].price.originalPrice").exists())
            .andExpect(jsonPath("$.content[0].price.currentPrice").exists())
            .andExpect(jsonPath("$.content[0].stock.unit").exists())
            .andExpect(jsonPath("$.content[0].category.id").exists())
            .andExpect(jsonPath("$.content[0].category.name").exists())
            .andExpect(jsonPath("$.content[0].manufacturer.id").exists())
            .andExpect(jsonPath("$.content[0].manufacturer.name").exists());
    }

    @Test
    void getAllTest02_withNameParam() throws Exception {
        // arrange
        String path = basePath;
        Product product = productsPersisted.get(0);
        String paramName = product.getName();
        int expectedResultSize = 1;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", paramName);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)))
            .andExpect(jsonPath("$.content[0].name", containsStringIgnoringCase(paramName)));
    }

    @Test
    void getAllTest03_withCategoryParam() throws Exception {
        // arrange
        String path = basePath;
        Product product = productsPersisted.get(0);
        String paramName = product.getCategory().getName();
        int expectedResultSize = 1;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("category", paramName);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)))
            .andExpect(jsonPath("$.content[0].category.name").value(paramName));
    }


    @Rollback
    @Test
    void getAllTest04_withMinPriceParam() throws Exception {
        // arrange
        seedProductsWithCustomizedPrices();
        String path = basePath;
        BigDecimal minPrice = BigDecimal.valueOf(150);
        int expectedResultSize = 3;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("minPrice", minPrice.toString());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @Rollback
    @Test
    void getAllTest05_withMaxPriceParam() throws Exception {
        // arrange
        seedProductsWithCustomizedPrices();
        String path = basePath;
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        int expectedResultSize = 3;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("maxPrice", maxPrice.toString());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @Rollback
    @Test
    void getAllTest06_withMinPriceAndMaxPrice() throws Exception {
        // arrange
        seedProductsWithCustomizedPrices();
        String path = basePath;
        BigDecimal minPrice = BigDecimal.valueOf(150);
        BigDecimal maxPrice = BigDecimal.valueOf(200);
        int expectedResultSize = 3;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("minPrice", minPrice.toString())
            .param("maxPrice", maxPrice.toString());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)));
    }

    @Rollback
    @Test
    void getAllTest07_withManufacturerName() throws Exception {
        // arrange
        String path = basePath;
        int expectedResultSize = 1;
        String manufacturerName = productsPersisted.get(0).getManufacturer().getName();

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("manufacturer", manufacturerName);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)))
            .andExpect(jsonPath("$.content[0].manufacturer.name").value(manufacturerName));
    }

    @Test
    void getAllTest08_withAllParams() throws Exception {
        // arrange
        String path = basePath;

        Product product = productsPersisted.get(0);
        String name = product.getName();
        String categoryName = product.getCategory().getName();
        BigDecimal minPrice = product.getPrice().getCurrentPrice().subtract(BigDecimal.ONE);
        BigDecimal maxPrice = product.getPrice().getCurrentPrice().add(BigDecimal.ONE);
        String manufacturerName = product.getManufacturer().getName();
        
        int expectedResultSize = 1;

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", name)
            .param("category", categoryName)
            .param("minPrice", minPrice.toString())
            .param("maxPrice", maxPrice.toString())
            .param("manufacturer", manufacturerName);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(expectedResultSize)))
            .andExpect(jsonPath("$.content[0].name").value(name))
            .andExpect(jsonPath("$.content[0].category.name").value(categoryName))
            .andExpect(jsonPath("$.content[0].manufacturer.name").value(manufacturerName));
    }

    private void seedProductsWithCustomizedPrices() {
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
    }
}