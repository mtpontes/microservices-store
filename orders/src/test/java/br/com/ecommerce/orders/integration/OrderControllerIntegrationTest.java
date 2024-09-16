package br.com.ecommerce.orders.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.orders.api.dto.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.api.http.ProductClient;
import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import br.com.ecommerce.orders.infra.entity.Product;
import br.com.ecommerce.orders.infra.exception.ProductOutOfStockDTO;
import br.com.ecommerce.orders.infra.repository.OrderRepository;
import br.com.ecommerce.orders.tools.annotations.ContextualizeUserWithRoles;
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

    @Autowired
    private JacksonTester<List<ProductDTO>> listProductCreateDTOJson;

    @BeforeAll
    static void setup(
        @Autowired OrderRepository orderRepository,
        @Autowired RandomUtils randomUtils
    ) {
        IntStream.range(0, 3)
            .forEach(flux -> {
                List<Product> products = List.of(
                    new Product(randomUtils.getRandomLong(), randomUtils.getRandomBigDecimal(), randomUtils.getRandomInt() +1),
                    new Product(randomUtils.getRandomLong(), randomUtils.getRandomBigDecimal(), randomUtils.getRandomInt() +1),
                    new Product(randomUtils.getRandomLong(), randomUtils.getRandomBigDecimal(), randomUtils.getRandomInt() +1)
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - createOrder - Should return status 201 and created order details")
    void createOrderTest01() throws IOException, Exception {
        // arrange
        var requestBody = List.of(new ProductDTO(1L, 100));

        var stockResponse = ResponseEntity.ok(List.of(new ProductOutOfStockDTO(null, null, null)));
        when(productClient.verifyStocks(anyList()))
            .thenReturn(stockResponse);

        var pricesResponse = ResponseEntity.ok(Set.of(new ProductAndPriceDTO(1L, BigDecimal.valueOf(100))));
        when(productClient.getPrices(anySet()))
            .thenReturn(pricesResponse);

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(listProductCreateDTOJson.write(requestBody).getJson())
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isCreated());

        verify(template, times(2)).convertAndSend(any(), any(), any(Object.class));
    }

    @Rollback
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - createOrder - Should return status 400 when the product list is empty or null")
    void createOrderTest02() throws IOException, Exception {
        // arrange
        List<ProductDTO> requestBody = List.of();
        var stockResponse = ResponseEntity.ok(List.of(new ProductOutOfStockDTO(null, null, null)));
        when(productClient.verifyStocks(anyList()))
            .thenReturn(stockResponse);

        var pricesResponse = ResponseEntity.ok(Set.of(new ProductAndPriceDTO(1L, BigDecimal.valueOf(100))));
        when(productClient.getPrices(anySet()))
            .thenReturn(pricesResponse);

        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(listProductCreateDTOJson.write(requestBody).getJson())
                .header("X-auth-user-id", "1")
        )
        // assert
        .andExpect(status().isBadRequest());
    }
    
    @Rollback
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
    @DisplayName("Unit - createOrder - Should return status 400 when X-auth-user-id header is missing")
    void createOrderTest03() throws IOException, Exception {
        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                // missing X-auth-user-id header
        )
        // assert
        .andExpect(status().isBadRequest());
    }

    @Rollback
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"ADMIN", "EMPLOYEE"})
    void createOrderTest04_withUnauthorizedRoles() throws IOException, Exception {
        // act
        mvc.perform(
            post(basePath)
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isForbidden());
    }

    @Rollback
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"ADMIN", "EMPLOYEE"})
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"ADMIN", "EMPLYEE"})
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"CLIENT"})
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
    @TestTemplate
    @ContextualizeUserWithRoles(roles = {"ADMIN", "EMPLYEE"})
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