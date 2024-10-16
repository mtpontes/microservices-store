package br.com.ecommerce.products.integration.api.controller.product;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.ecommerce.products.annotations.ControllerIntegrationTest;
import br.com.ecommerce.products.api.dto.product.ProductUnitsRequestedDTO;
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
class InternalProductControllerIntegrationTest {

    private final String basePath = "/internal/products";
    private static List<Product> productsPersisted;
    private static Manufacturer manufacturer;
    private static Category category;
    private static Stock stock;

    @MockBean
    private RabbitTemplate template;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<List<ProductUnitsRequestedDTO>> productUnitsRequestedDTOJson;

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
        productsPersisted = Stream.generate(() -> {
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
            .limit(3)
            .collect(Collectors.toList());
    }

    @Test
    void verifyStocksTest01_withoutExceedingTheAvailableStock() throws Exception {
        // arrange
        String path = basePath + "/stocks";

        var requestBody = IntStream.range(0, 2)
            .mapToObj(flux -> {
                Product product = productsPersisted.get(flux);
                return new ProductUnitsRequestedDTO(product.getId(), product.getStock().getUnit());
            })
            .collect(Collectors.toList());

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(productUnitsRequestedDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);


        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void verifyStocksTest02_exceedingAvailableStock() throws Exception {
        // arrange
        String path = basePath + "/stocks";

        var requestBody = IntStream.range(0, 2)
            .mapToObj(flux -> {
                Product product = productsPersisted.get(flux);
                return new ProductUnitsRequestedDTO(product.getId(), product.getStock().getUnit() * 2);
            })
            .collect(Collectors.toList());

        // act
        var requestMock = MockMvcRequestBuilders.post(path)
            .contentType(MediaType.APPLICATION_JSON)
            .content(productUnitsRequestedDTOJson.write(requestBody).getJson());
        ResultActions act = mvc.perform(requestMock);


        // assert
        act.andExpect(status().isMultiStatus())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getPricesTest01_exceedingAvailableStock() throws Exception {
        // arrange
        String path = basePath + "/prices";
        BigDecimal expectedPrice1 = productsPersisted.get(0).getPrice().getCurrentPrice();
        BigDecimal expectedPrice2 = productsPersisted.get(1).getPrice().getCurrentPrice();

        // act
        var requestMock = MockMvcRequestBuilders.get(path)
            .contentType(MediaType.APPLICATION_JSON)
            .param("productIds", "1")
            .param("productIds", "2");
        ResultActions act = mvc.perform(requestMock);

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.1.price").value(expectedPrice1.doubleValue()))
            .andExpect(jsonPath("$.2.price").value(expectedPrice2.doubleValue()));
    }
}