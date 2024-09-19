package br.com.ecommerce.orders.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfigBeans.class)
@AutoConfigureJsonTesters
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class AdminOrderControllerIntegrationTest {

    private final String basePath = "/admin/orders";
    private static List<Order> ordersPersisted = new ArrayList<>();

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
                ordersPersisted.add(orderRepository.save(order));
            });
    }
    

    @TestWithRoles(roles = {"ADMIN"})
    void getAllBasicsInfoOrdersByUserTest01() throws Exception {
        // act
        mvc.perform(
            get(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isOk());
    }

    @TestWithRoles(roles = {"CLIENT", "EMPLOYEE"})
    void getAllBasicsInfoOrdersByUserTest02_withUnauthorizedRoles() throws Exception {
        mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @TestWithRoles(roles = {"ADMIN"})
    void getOrderByIdAndUserIdTest01() throws Exception {
        Long orderId = ordersPersisted.get(0).getId();
        Long userId = ordersPersisted.get(0).getUserId();

        mvc.perform(
            get(String.format("%s/%d/%d", basePath, orderId, userId))
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isOk());
    }

    @TestWithRoles(roles = {"CLIENT", "EMPLOYEE"})
    void getOrderByIdAndUserIdTest02_withUnauthorizedRoles() throws Exception {
        mvc.perform(
            get(basePath + "/1/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @Rollback
    @TestWithRoles(roles = {"ADMIN"})
    @DisplayName("Unit - cancelOrder - Should return status 204")
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
    @TestWithRoles(roles = {"CLIENT", "EMPLOYEE"})
    void cancelOrderTest02_withUnauthorizedRoles() throws IOException, Exception {
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