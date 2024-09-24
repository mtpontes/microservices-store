package br.com.ecommerce.orders.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.common.annotations.TestWithRoles;
import br.com.ecommerce.orders.api.client.ProductClient;
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
class ClientOrderControllerIntegrationTest {

    private final String basePath = "/client/orders";
    private Order orderPersisted = null;

    @MockBean
    private RabbitTemplate template;
    @MockBean
    private ProductClient productClient;

    @Autowired
    private MockMvc mvc;

    @Autowired 
    private OrderRepository repository;
    @Autowired 
    private RandomUtils randomUtils;

    @BeforeEach
    void setup() {
        IntStream.range(0, 3)
            .forEach(flux -> {
                List<Product> products = List.of(
                    new Product(
                        randomUtils.getRandomString(),
                        randomUtils.getRandomString(10),
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt()),
                    new Product(
                        randomUtils.getRandomString(),
                        randomUtils.getRandomString(10), 
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt()),
                    new Product(
                        randomUtils.getRandomString(), 
                        randomUtils.getRandomString(10),
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt())
                );

                Order order = new OrderTestBuilder()
                    .id(randomUtils.getRandomString())
                    .userId(randomUtils.getRandomString())
                    .products(products)
                    .total(randomUtils.getRandomBigDecimal())
                    .status(OrderStatus.AWAITING_PAYMENT)
                    .build();
                orderPersisted = repository.save(order);
            });
    }

    @AfterEach
    void cleanup() {
        this.repository.deleteAll();
    }


    @TestWithRoles(roles = {"CLIENT"})
    void getAllBasicsInfoOrdersByUserTest01_missingHeader() throws Exception {
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isUnauthorized());
    }

    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void getAllBasicsInfoOrdersByUserTest02_withUnauthorizedRoles() throws Exception {
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", orderPersisted.getUserId())
        )
        // assert
        .andExpect(status().isForbidden());
    }
    
    @TestWithRoles(roles = {"CLIENT"})
    void getOrderByIdAndUserIdTest01_missingHeader() throws Exception {
        mvc.perform(
            get(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isUnauthorized());
    }

    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void getOrderByIdAndUserIdTest02_withUnauthorizedRoles() throws Exception {
        // arrange
        String orderId = orderPersisted.getId();

        mvc.perform(
            get(basePath + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", orderPersisted.getUserId())
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @TestWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - cancelOrder - Should return status 204")
    void cancelOrderTest01() throws IOException, Exception {
        // arrange
        String orderId = orderPersisted.getId();

        // act
        mvc.perform(
            patch(basePath + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isNoContent());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void cancelOrderTest02_missingHeader() throws IOException, Exception {
        // act
        mvc.perform(
            patch(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isUnauthorized());
    }

    @TestWithRoles(roles = {"ADMIN", "EMPLYEE"})
    void cancelOrderTest03_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            patch(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isForbidden());
    }
}