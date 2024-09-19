package br.com.ecommerce.orders.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
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
import br.com.ecommerce.orders.tools.utils.RandomUtils;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfigBeans.class)
@AutoConfigureJsonTesters
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class OrderControllerIntegrationTest {

    private final String basePath = "/orders";

    @MockBean
    private RabbitTemplate template;
    @MockBean
    private ProductClient productClient;
    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setup(
        @Autowired OrderRepository orderRepository,
        @Autowired RandomUtils randomUtils
    ) {
        IntStream.range(0, 3)
            .forEach(flux -> {
                List<Product> products = List.of(
                    new Product(
                        randomUtils.getRandomLong(),
                        randomUtils.getRandomString(10),
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt()),
                    new Product(
                        randomUtils.getRandomLong(),
                        randomUtils.getRandomString(10), 
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt()),
                    new Product(
                        randomUtils.getRandomLong(), 
                        randomUtils.getRandomString(10),
                        randomUtils.getRandomBigDecimal(), 
                        randomUtils.getRandomInt())
                );

                Order order = new OrderTestBuilder()
                    .userId(randomUtils.getRandomLong())
                    .products(products)
                    .total(randomUtils.getRandomBigDecimal())
                    .status(OrderStatus.AWAITING_PAYMENT)
                    .build();
                orderRepository.save(order);
            });
    }


    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - getAllBasicsInfoOrdersByUser - Should return statud 400 when X-auth-user-id header is missing")
    void getAllBasicsInfoOrdersByUserTest01() throws Exception {
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isBadRequest());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void getAllBasicsInfoOrdersByUserTest02_withUnauthorizedRoles() throws Exception {
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isForbidden());
    }
    
    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - getAllOrdersByUserId - Should return statud 400 when X-auth-user-id header is missing")
    void getOrderByIdAndUserIdTest01() throws Exception {
        mvc.perform(
            get(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isBadRequest());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN", "EMPLYEE"})
    @DisplayName("Unit - getAllOrdersByUserId - Should return statud 400 when X-auth-user-id header is missing")
    void getOrderByIdAndUserIdTest02_withUnauthorizedRoles() throws Exception {
        mvc.perform(
            get(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - cancelOrder - Should return status 200")
    void cancelOrderTest01() throws IOException, Exception {
        // act
        mvc.perform(
            patch(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isNoContent());
    }

    @Rollback
    @TestWithRoles(roles = {"CLIENT"})
    void cancelOrderTest02_missingHeader() throws IOException, Exception {
        // act
        mvc.perform(
            patch(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isBadRequest());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN", "EMPLYEE"})
    @DisplayName("Unit - cancelOrder - Should return status 200")
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