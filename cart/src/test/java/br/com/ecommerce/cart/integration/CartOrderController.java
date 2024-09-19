package br.com.ecommerce.cart.integration;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import br.com.ecommerce.cart.api.client.OrderClient;
import br.com.ecommerce.cart.api.dto.order.OrderDataDTO;
import br.com.ecommerce.cart.config.MongoDBTestContainer;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.infra.repository.CartRepository;
import br.com.ecommerce.common.annotations.TestWithRoles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MongoDBTestContainer.class)
@AutoConfigureJsonTesters
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class CartOrderController {

    final String basePath = "/cart-to-order";
    static Cart userCartPersisted = null;
    static Cart emptyUserCart = null;
    static Cart anonCartPersisted = null;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JacksonTester<Set<String>> collectionOfIdsJson;
    @Autowired
    private CartRepository cartRepository;

    @MockBean
    private OrderClient orderClient;

    @BeforeEach
    void setup(@Autowired CartRepository repository) {
        Product product1 = new Product("1", 10);
        Cart validCart = new Cart("userId");
        validCart.addProduct(product1);
        userCartPersisted = repository.save(validCart);

        Cart emptyCart = new Cart("emptyCart");
        emptyUserCart = repository.save(emptyCart);

        Product product2 = new Product("2", 10);
        Cart anonCart = new Cart(product2);
        anonCartPersisted = repository.save(anonCart);
    }

    @AfterEach
    void cleanup() {
        cartRepository.deleteAll();
    }


    @TestWithRoles(roles = {"CLIENT"})
    void createTest01() throws Exception {
        // arrange
        Set<String> productIds = new HashSet<>(Set.of(userCartPersisted.getProducts().iterator().next().getId()));
        OrderDataDTO responseBody = new OrderDataDTO();

        when(orderClient.createOrder(anyString(), anySet()))
            .thenReturn(responseBody);

        // act
        String requestBody = collectionOfIdsJson.write(productIds).getJson();
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", userCartPersisted.getUserId())
            .content(requestBody));

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").hasJsonPath())
            .andExpect(jsonPath("$.products").hasJsonPath())
            .andExpect(jsonPath("$.total").hasJsonPath())
            .andExpect(jsonPath("$.status").hasJsonPath())
            .andExpect(jsonPath("$.createdAt").hasJsonPath());
        
        Cart cart = cartRepository.findById(userCartPersisted.getUserId()).get();
        assertTrue(cart.getProducts().isEmpty());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void createTest02_withEmptyCart() throws Exception {
        // arrange
        Set<String> productIds = new HashSet<>(Set.of(userCartPersisted.getProducts().iterator().next().getId()));
        OrderDataDTO responseBody = new OrderDataDTO();
        String invalidId = emptyUserCart.getUserId();

        when(orderClient.createOrder(anyString(), anySet()))
            .thenReturn(responseBody);

        // act
        String requestBody = collectionOfIdsJson.write(productIds).getJson();
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", invalidId)
            .content(requestBody));

        // assert
        act.andExpect(status().isBadRequest());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void createTest02_withUnexistentCart() throws Exception {
        // arrange
        Set<String> productIds = new HashSet<>(Set.of(userCartPersisted.getProducts().iterator().next().getId()));
        OrderDataDTO responseBody = new OrderDataDTO();
        String invalidId = "randominvalidid";
        when(orderClient.createOrder(anyString(), anySet()))
            .thenReturn(responseBody);

        // act
        String requestBody = collectionOfIdsJson.write(productIds).getJson();
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", invalidId)
            .content(requestBody));

        // assert
        act.andExpect(status().isNotFound());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void createTest03_withAnonymousCart() throws Exception {
        // arrange
        Set<String> productIds = new HashSet<>(Set.of(anonCartPersisted.getProducts().iterator().next().getId()));
        OrderDataDTO responseBody = new OrderDataDTO();
        when(orderClient.createOrder(anyString(), anySet()))
            .thenReturn(responseBody);

        // act
        String requestBody = collectionOfIdsJson.write(productIds).getJson();
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", anonCartPersisted)
            .content(requestBody));

        // assert
        act.andExpect(status().isNotFound());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void createTest04_bodyIsEmptyList() throws Exception {
        // arrange
        Set<String> productIds = new HashSet<>(Set.of());
        OrderDataDTO responseBody = new OrderDataDTO();
        String invalidId = emptyUserCart.getUserId();

        when(orderClient.createOrder(anyString(), anySet()))
            .thenReturn(responseBody);

        // act
        String requestBody = collectionOfIdsJson.write(productIds).getJson();
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", invalidId)
            .content(requestBody));

        // assert
        act.andExpect(status().isBadRequest());
    }

    @TestWithRoles(roles = {"CLIENT"})
    void createTest05_stringsOfBodyIsBlank() throws Exception {
        // arrange
        Set<String> productIds = new HashSet<>(Set.of(""));
        OrderDataDTO responseBody = new OrderDataDTO();
        String invalidId = emptyUserCart.getUserId();

        when(orderClient.createOrder(anyString(), anySet()))
            .thenReturn(responseBody);

        // act
        String requestBody = collectionOfIdsJson.write(productIds).getJson();
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", invalidId)
            .content(requestBody));

        // assert
        act.andExpect(status().isBadRequest());
    }
}