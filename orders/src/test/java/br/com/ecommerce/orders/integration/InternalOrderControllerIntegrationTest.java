package br.com.ecommerce.orders.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.common.annotations.TestCustomWithMockUser;
import br.com.ecommerce.orders.api.client.ProductClient;
import br.com.ecommerce.orders.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.orders.api.dto.product.ProductAndUnitDTO;
import br.com.ecommerce.orders.api.dto.product.ProductOutOfStockDTO;
import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import br.com.ecommerce.orders.infra.entity.Product;
import br.com.ecommerce.orders.infra.repository.OrderRepository;
import br.com.ecommerce.orders.tools.builder.OrderTestBuilder;
import br.com.ecommerce.orders.tools.config.TestConfigBeans;
import br.com.ecommerce.orders.tools.testcontainers.MongoDBTestContainer;
import br.com.ecommerce.orders.tools.utils.RandomUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Import({TestConfigBeans.class, MongoDBTestContainer.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class InternalOrderControllerIntegrationTest {

    private final String basePath = "/internal/orders";

    @MockBean
    private RabbitTemplate template;
    @MockBean
    private ProductClient productClient;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private RandomUtils randomUtils;
    @Autowired
    private JacksonTester<List<ProductAndUnitDTO>> listOfProductAndUnitDTOJson;

    @BeforeAll
    static void setup(
        @Autowired OrderRepository orderRepository,
        @Autowired RandomUtils randomUtils
    ) {
        IntStream.range(0, 3)
            .forEach(flux -> {
                List<Product> products = List.of(
                    new Product(
                        randomUtils.getRandomString(),
                        randomUtils.getRandomString(10),
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt(),
                        randomUtils.getRandomString()),
                    new Product(
                        randomUtils.getRandomString(),
                        randomUtils.getRandomString(10), 
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt(),
                        randomUtils.getRandomString()),
                    new Product(
                        randomUtils.getRandomString(), 
                        randomUtils.getRandomString(10),
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt(),
                        randomUtils.getRandomString())
                );

                Order order = new OrderTestBuilder()
                    .id(randomUtils.getRandomString())
                    .userId(randomUtils.getRandomString())
                    .products(products)
                    .total(randomUtils.getRandomBigDecimal())
                    .status(OrderStatus.AWAITING_PAYMENT)
                    .build();
                orderRepository.save(order);
            });
    }
    

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    @DisplayName("Unit - createOrder - Should return status 201 and created order details")
    void createOrderTest01() throws IOException, Exception {
        // arrange
        var requestBody = List.of(new ProductAndUnitDTO("1", 100));

        when(productClient.verifyStocks(anySet()))
            .thenReturn(Collections.emptySet());

        Set<String> listOfIds = requestBody.stream().map(ProductAndUnitDTO::getId).collect(Collectors.toSet());
        InternalProductDataDTO nameAndPrice = new InternalProductDataDTO("any name", BigDecimal.ONE, randomUtils.getRandomString());
        Map<String, InternalProductDataDTO> priceMap = listOfIds.stream()
            .collect(Collectors.toMap(id -> id, id -> nameAndPrice));
        when(productClient.getPrices(eq(listOfIds)))
            .thenReturn(priceMap);

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(listOfProductAndUnitDTOJson.write(requestBody).getJson())
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isCreated());

        verify(template, times(2)).convertAndSend(any(), any(), any(Object.class));
    }

    @Rollback
    @TestCustomWithMockUser(roles = {"CLIENT"})
    @DisplayName("Unit - createOrder - Should return status 400 when the product list is empty or null")
    void createOrderTest02() throws IOException, Exception {
        // arrange
        List<ProductAndUnitDTO> requestBody = List.of();
        var stockResponse = Set.of(new ProductOutOfStockDTO(null, null, null));
        when(productClient.verifyStocks(anySet()))
            .thenReturn(stockResponse);

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(listOfProductAndUnitDTOJson.write(requestBody).getJson())
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isBadRequest());
    }

    @Test
    void createOrderTest03_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Forwarded-By", "gateway")
        )
        // assert
        .andExpect(status().isForbidden());
    }
}