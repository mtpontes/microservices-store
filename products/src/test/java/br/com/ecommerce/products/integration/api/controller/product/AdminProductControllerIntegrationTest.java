package br.com.ecommerce.products.integration.api.controller.product;

import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.ecommerce.common.annotations.TestCustomWithMockUser;
import br.com.ecommerce.products.annotations.ControllerIntegrationTest;
import br.com.ecommerce.products.api.dto.product.CreateProductDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.EndOfPromotionDTO;
import br.com.ecommerce.products.api.dto.product.SchedulePromotionDTO;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @Autowired
    private JacksonTester<SchedulePromotionDTO> schedulePromotionDTOJson;

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
        productsPersisted = Stream.generate(() -> {
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
            .limit(3)
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

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePriceTest01_withValidValues() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(BigDecimal.valueOf(200));

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
            .andExpect(jsonPath("$.price.currentPrice").value(requestBody.getPrice()))
            .andExpect(jsonPath("$.price.originalPrice").value(requestBody.getPrice()))
            .andExpect(jsonPath("$.price.promotionalPrice").isEmpty())
            .andExpect(jsonPath("$.price.onPromotion").value(false))
            .andExpect(jsonPath("$.price.startPromotion").isEmpty())
            .andExpect(jsonPath("$.price.endPromotion").isEmpty());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePriceTest02_withInvalidValues() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(null);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePriceTest03_withBothValuesEqual() throws Exception {
        // arrange
        Product productPersisted = productsPersisted.get(0);
        Long productId = productPersisted.getId();
        String path = String.format("%s/%s/prices", basePath, productId);
        var requestBody = new UpdatePriceDTO(BigDecimal.valueOf(Math.negateExact(1)));

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should not cause errors when setting the original price lower than the current promotional price")
    void updatePriceTest04_withOriginalPriceLowerThanPromotionalPrice() throws Exception {
        // arrange
        final Product productPersisted = productsPersisted.get(0);
        final Long productId = productPersisted.getId();
        final String path = String.format("%s/%s/prices", basePath, productId);

        final BigDecimal newPrice = productPersisted.getPrice().getPromotionalPrice();
        final var requestBody = new UpdatePriceDTO(newPrice); // new price is the same as the promotional price

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.price.currentPrice").value(requestBody.getPrice()))
            .andExpect(jsonPath("$.price.originalPrice").value(requestBody.getPrice()))
            .andExpect(jsonPath("$.price.promotionalPrice").isEmpty())
            .andExpect(jsonPath("$.price.onPromotion").value(false))
            .andExpect(jsonPath("$.price.startPromotion").isEmpty())
            .andExpect(jsonPath("$.price.endPromotion").isEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updatePriceTest05_withAllAllowedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        final var responseStatusCode = act.andReturn().getResponse().getStatus();
        assertNotEquals(403, responseStatusCode);
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

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePromotionalPriceTest01_withValidValues() throws Exception {
        // arrange
        final Product producPersisted = productsPersisted.get(0);
        final var productId = producPersisted.getId();
        final var promotionalPrice = producPersisted.getPrice().getPromotionalPrice();
        final String path = String.format("%s/%s/prices/promotion", basePath, productId);
        final var requestBody = new UpdatePriceDTO(promotionalPrice.divide(BigDecimal.valueOf(2)));

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        final var expectedCurrentPrice = producPersisted.getPrice().getOriginalPrice().doubleValue();
        final var expectedOriginalPrice = producPersisted.getPrice().getOriginalPrice().doubleValue();
        final var expectedPromotionalPrice = requestBody.getPrice().doubleValue();

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.price.currentPrice").value(expectedCurrentPrice))
            .andExpect(jsonPath("$.price.originalPrice").value(expectedOriginalPrice))
            .andExpect(jsonPath("$.price.promotionalPrice").value(expectedPromotionalPrice))
            .andExpect(jsonPath("$.price.onPromotion").value(false))
            .andExpect(jsonPath("$.price.startPromotion").isEmpty())
            .andExpect(jsonPath("$.price.endPromotion").isEmpty());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePromotionalPriceTest02_withPromotionalPriceGreaterThanOriginalPrice() throws Exception {
        // arrange
        final Product producPersisted = productsPersisted.get(0);
        final var productId = producPersisted.getId();
        final var originalPrice = producPersisted.getPrice().getOriginalPrice();
        final String path = String.format("%s/%s/prices/promotion", basePath, productId);
        final var requestBodyWithEqualOriginalPrice = new UpdatePriceDTO(originalPrice);
        final var requestBodyWithGreaterOriginalPrice = new UpdatePriceDTO(originalPrice.add(BigDecimal.ONE));

        // act
        var requestMock_equalsOriginalPrice = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBodyWithEqualOriginalPrice).getJson());
        ResultActions act_equalOriginalPrice = mvc.perform(requestMock_equalsOriginalPrice);

        var requestMock_greaterOriginalPrice = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBodyWithGreaterOriginalPrice).getJson());
        ResultActions act_greaterOriginalPrice = mvc.perform(requestMock_greaterOriginalPrice);

        // assert
        act_equalOriginalPrice.andExpect(status().isBadRequest());
        act_greaterOriginalPrice.andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePromotionalPriceTest03_acceptsNullAsValue() throws Exception {
        // arrange
        final Product producPersisted = productsPersisted.get(0);
        final var productId = producPersisted.getId();
        final String path = String.format("%s/%s/prices/promotion", basePath, productId);
        final var requestBodyWithEqualOriginalPrice = new UpdatePriceDTO(null);

        // act
        var requestMock_equalsOriginalPrice = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBodyWithEqualOriginalPrice).getJson());
        ResultActions act_equalOriginalPrice = mvc.perform(requestMock_equalsOriginalPrice);

        // assert
        act_equalOriginalPrice.andExpect(status().isOk())
            .andExpect(jsonPath("$.price.promotionalPrice").isEmpty());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void updatePromotionalPriceTest04_withInvalidValues() throws Exception {
        // arrange
        final Product producPersisted = productsPersisted.get(0);
        final var productId = producPersisted.getId();
        final var promotionalPrice = producPersisted.getPrice().getPromotionalPrice();
        final String path = String.format("%s/%s/prices/promotion", basePath, productId);
        final var requestBody_withZero = new UpdatePriceDTO(BigDecimal.ZERO); // with zero
        final var requestBody_withNegative = new UpdatePriceDTO(promotionalPrice.negate()); // with negative

        // act
        var requestMock_equalsOriginalPrice = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody_withZero).getJson());
        ResultActions act_withZero = mvc.perform(requestMock_equalsOriginalPrice);

        var requestMock_greaterOriginalPrice = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePriceDTOJson.write(requestBody_withNegative).getJson());
        ResultActions act_withNegative = mvc.perform(requestMock_greaterOriginalPrice);

        // assert
        act_withZero.andExpect(status().isBadRequest());
        act_withNegative.andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updatePromotionalPriceTest05_withAllAllowedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        final var responseStatusCode = act.andReturn().getResponse().getStatus();
        assertNotEquals(403, responseStatusCode);
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void updatePromotionalPriceTest06_withUnauthorizedRoles() throws Exception {
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

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void iniciatePromotionTest01() throws Exception {
        // arrange
        final Product product = productsPersisted.get(0);
        final var productId = product.getId();
        final var originalPrice = product.getPrice().getOriginalPrice();
        final var promotionalPrice = product.getPrice().getPromotionalPrice();

        final LocalDateTime date = LocalDateTime.now().plusDays(10).withSecond(00).withNano(0);
        final EndOfPromotionDTO requestBody = new EndOfPromotionDTO(date);

        // act
        String path = String.format("%s/%s/prices/promotion/start", basePath, productId);
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
            .andExpect(jsonPath("$.price.startPromotion").isEmpty())
            .andExpect(jsonPath("$.price.endPromotion").value(requestBody.getEndPromotion().toString()));
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void iniciatePromotionTest02_withPastDate() throws Exception {
        // arrange
        final Product product = productsPersisted.get(0);
        final var productId = product.getId();

        final LocalDateTime date = LocalDateTime.now().minusDays(10);
        final EndOfPromotionDTO requestBody = new EndOfPromotionDTO(date);

        // act
        String path = String.format("%s/%s/prices/promotion/start", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(endOfPromotionDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void iniciatePromotionTest04_withAllAllowedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices/promotion/start", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        final var responseStatusCode = act.andReturn().getResponse().getStatus();
        assertNotEquals(403, responseStatusCode);
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void iniciatePromotionTest05_withUnauthorizedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices/promotion/start", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void schedulePromotionTest01() throws Exception {
        // arrange
        final Product product = productsPersisted.get(0);
        final var productId = product.getId();

        final LocalDateTime start = LocalDateTime.now().plusDays(1).withSecond(00).withNano(0);
        final LocalDateTime end = LocalDateTime.now().plusDays(10).withSecond(00).withNano(0);
        final SchedulePromotionDTO requestBody = new SchedulePromotionDTO(start, end);

        // act
        final String path = String.format("%s/%s/prices/promotion/schedule", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(schedulePromotionDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // assert
        act
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.price.onPromotion").value(false))
            .andExpect(jsonPath("$.price.startPromotion").value(start.format(formatter)))
            .andExpect(jsonPath("$.price.endPromotion").value(end.format(formatter)));
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void schedulePromotionTest01_withPastDates() throws Exception {
        // arrange
        final Product product = productsPersisted.get(0);
        final var productId = product.getId();

        final LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        final LocalDateTime validDate = LocalDateTime.now().plusDays(1).withSecond(00).withNano(0);
        final SchedulePromotionDTO requestBody_1 = new SchedulePromotionDTO(validDate, pastDate);
        final SchedulePromotionDTO requestBody_2 = new SchedulePromotionDTO(pastDate, validDate);

        // act
        final String path = String.format("%s/%s/prices/promotion/schedule", basePath, productId);
        var requestMock_1 = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(schedulePromotionDTOJson.write(requestBody_1).getJson());
        var requestMock_2 = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(schedulePromotionDTOJson.write(requestBody_2).getJson());
        ResultActions act_1 = mvc.perform(requestMock_1);
        ResultActions act_2 = mvc.perform(requestMock_2);

        // assert
        act_1.andExpect(status().isBadRequest());
        act_2.andExpect(status().isBadRequest());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void schedulePromotionTest03_withAllAllowedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices/promotion/schedule", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        final var responseStatusCode = act.andReturn().getResponse().getStatus();
        assertNotEquals(403, responseStatusCode);
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void schedulePromotionTest04_withUnauthorizedRoles() throws Exception {
        // arrange
        Long productId = 1L;
        String path = String.format("%s/%s/prices/promotion/schedule", basePath, productId);

        // act
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void finalizePromotionTest01() throws Exception {
        // arrange
        final Product product = productsPersisted.get(0);
        final var productId = product.getId();
        final var originalPrice = product.getPrice().getOriginalPrice();
        final var promotionalPrice = product.getPrice().getPromotionalPrice();
        
        // act
        String path = String.format("%s/%s/prices/promotion/end", basePath, productId);
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
            .andExpect(jsonPath("$.price.startPromotion").isEmpty())
            .andExpect(jsonPath("$.price.endPromotion").isEmpty());
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void finalizePromotionTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        
        // act
        String path = String.format("%s/%s/prices/promotion/end", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        final var responseStatusCode = act.andReturn().getResponse().getStatus();
        assertNotEquals(403, responseStatusCode);
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    void finalizePromotionTest03_withUnauthorizedRoles() throws Exception {
        // arrange
        Product product = productsPersisted.get(0);
        Long productId = product.getId();
        
        // act
        String path = String.format("%s/%s/prices/promotion/end", basePath, productId);
        var requestMock = MockMvcRequestBuilders.put(path)
            .contentType(MediaType.APPLICATION_JSON);
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isForbidden());
    }
}