package br.com.ecommerce.products.integration.api.controller.product;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.ecommerce.common.annotations.TestCustomWithMockUser;
import br.com.ecommerce.products.annotations.ControllerIntegrationTest;
import br.com.ecommerce.products.api.dto.product.CreateProductDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.EndOfPromotionDTO;
import br.com.ecommerce.products.api.dto.product.UpdatePriceDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductDTO;
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
import br.com.ecommerce.products.utils.util.RandomUtils;
import br.com.ecommerce.products.utils.util.StockUtils;

@ControllerIntegrationTest
class AdminProductControllerIntegrationTest {

    private static List<Product> productsPersisted;
    private final String basePath = "/admin/products";

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private RandomUtils randomUtils;

    @Autowired
    private JacksonTester<CreateProductDTO> createProductDTOJson;
    @Autowired
    private JacksonTester<UpdateProductDTO> updateProductDTOJson;
    @Autowired
    private JacksonTester<DataStockDTO> updateStockDTOJson;
    @Autowired
    private JacksonTester<UpdatePriceDTO> updatePriceDTOJson;
    @Autowired
    private JacksonTester<EndOfPromotionDTO> endOfPromotionDTOJson;

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

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createProductTest01_withValidData() throws Exception {
        // arrange
        String path = basePath;
        Long categoryId = productsPersisted.get(0).getCategory().getId();
        Long manufacturerId = productsPersisted.get(0).getManufacturer().getId();
        var requestBody = new CreateProductDTO(
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            categoryId,
            manufacturerId
        );

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createProductDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName()))
            .andExpect(jsonPath("$.description").value(requestBody.getDescription()))
            .andExpect(jsonPath("$.specs").value(requestBody.getSpecs()))
            .andExpect(jsonPath("$.category").exists())
            .andExpect(jsonPath("$.manufacturer").exists());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createProductTest03_withValidData() throws Exception {
        // arrange
        String path = basePath;
        Long categoryId = productsPersisted.get(0).getCategory().getId();
        Long manufacturerId = productsPersisted.get(0).getManufacturer().getId();
        var requestBody = new CreateProductDTO(
            randomUtils.getRandomString(),
            null,
            null,
            categoryId,
            manufacturerId
        );

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createProductDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName()))
            .andExpect(jsonPath("$.description").value(""))
            .andExpect(jsonPath("$.specs").value(""))
            .andExpect(jsonPath("$.category").exists())
            .andExpect(jsonPath("$.manufacturer").exists());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createProductTest04_withExistentName() throws Exception {
        // arrange
        String path = basePath;
        String existentName = productsPersisted.get(0).getName();
        Long categoryId = productsPersisted.get(0).getCategory().getId();
        Long manufacturerId = productsPersisted.get(0).getManufacturer().getId();
        var requestBody = new CreateProductDTO(
            existentName,
            null,
            null,
            categoryId,
            manufacturerId
        );

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createProductDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void createProductTest06_withUnauthorizedRoles() throws Exception {
        // arrange
        String path = basePath;

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateTest01_withValidData() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s", basePath, productId);
        var requestBody = new UpdateProductDTO(
            randomUtils.getRandomString(),
            randomUtils.getRandomString(),
            randomUtils.getRandomString()
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateProductDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(requestBody.getName()))
            .andExpect(jsonPath("$.description").value(requestBody.getDescription()))
            .andExpect(jsonPath("$.specs").value(requestBody.getSpecs()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateTest02_withNullValues() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s", basePath, productId);
        var requestBody = new UpdateProductDTO(
            null,
            null,
            null
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateProductDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.description").isNotEmpty())
            .andExpect(jsonPath("$.specs").isNotEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateTest03_withExistentName() throws Exception {
        // arrange
        Long productId = 1L;
        String existentName = productsPersisted.get(0).getName();
        String path = String.format("%s/%s", basePath, productId);
        var requestBody = new UpdateProductDTO(
            existentName,
            randomUtils.getRandomString(),
            randomUtils.getRandomString()
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateProductDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updateTest04_withUnauthorizedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateStockTest01_addUnits() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/stocks", basePath, productId);

        int sumUnits = 200;
        var requestBody = new DataStockDTO(sumUnits);
        int expectedStockUnits = productsPersisted.get(0).getStock().getUnit() + sumUnits;

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateStockDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.unit").value(expectedStockUnits));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateStockTest02_subtractUnits() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        String path = String.format("%s/%s/stocks", basePath, productId);

        int originalUnits = product.getStock().getUnit();
        int subtractUnits = originalUnits - (originalUnits + 1000);
        var requestBody = new DataStockDTO(subtractUnits);
        int expectedUnits = 0;

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateStockDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.unit").value(expectedUnits));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateStockTest03_withInvalidValue() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        String path = String.format("%s/%s/stocks", basePath, productId);

        var requestBody = new DataStockDTO(null);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateStockDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message.unit").exists());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updateStockTest03_withUnauthorizedRoles() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        String path = String.format("%s/%s/stocks", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updatePriceTest01_withValidValues() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(
            BigDecimal.valueOf(200),
            BigDecimal.valueOf(100)
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.price.originalPrice").value(requestBody.getOriginalPrice()))
            .andExpect(jsonPath("$.price.promotionalPrice").value(requestBody.getPromotionalPrice()))
            .andExpect(jsonPath("$.price.currentPrice").value(requestBody.getOriginalPrice()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updatePriceTest02_withInvalidValues() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(
            null,
            BigDecimal.valueOf(100)
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updatePriceTest03_withBothValuesEqual() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(100)
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updatePriceTest04_withOriginalPriceLowerThanPromotionalPrice01() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(
            BigDecimal.valueOf(99),
            BigDecimal.valueOf(100)
        );

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updatePriceTest05_withOriginalPriceLowerThanPromotionalPrice() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updatePriceTest06_withUnauthorizedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void switchCurrentPriceToOriginalTest01() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        BigDecimal originalPrice = product.getPrice().getOriginalPrice();
        BigDecimal promotionalPrice = product.getPrice().getPromotionalPrice();

        
        // act
        String path = String.format("%s/%s/prices/switch-to-original", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.price.currentPrice").value(originalPrice.doubleValue()))
            .andExpect(jsonPath("$.price.originalPrice").value(originalPrice.doubleValue()))
            .andExpect(jsonPath("$.price.promotionalPrice").value(promotionalPrice.doubleValue()))
            .andExpect(jsonPath("$.price.onPromotion").value(false))
            .andExpect(jsonPath("$.price.endOfPromotion").isEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void switchCurrentPriceToOriginalTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        
        // act
        String path = String.format("%s/%s/prices/switch-to-original", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void switchCurrentPriceToPromotionalPriceTest01() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        BigDecimal originalPrice = product.getPrice().getOriginalPrice();
        BigDecimal promotionalPrice = product.getPrice().getPromotionalPrice();

        LocalDateTime date = LocalDateTime.now().plusDays(10).withSecond(00).withNano(0);
        EndOfPromotionDTO requestBody = new EndOfPromotionDTO(date);

        // act
        String path = String.format("%s/%s/prices/switch-to-promotional", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(endOfPromotionDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.price.originalPrice").value(originalPrice.doubleValue()))
            .andExpect(jsonPath("$.price.promotionalPrice").value(promotionalPrice.doubleValue()))
            .andExpect(jsonPath("$.price.currentPrice").value(promotionalPrice.doubleValue()))
            .andExpect(jsonPath("$.price.onPromotion").value(true))
            .andExpect(jsonPath("$.price.endOfPromotion").value(requestBody.getEndOfPromotion().toString()));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void switchCurrentPriceToPromotionalPriceTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();

        // act
        String path = String.format("%s/%s/prices/switch-to-promotional", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }
}