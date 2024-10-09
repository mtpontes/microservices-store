package br.com.ecommerce.cart.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import br.com.ecommerce.cart.api.client.OrderClient;
import br.com.ecommerce.cart.api.client.ProductClient;
import br.com.ecommerce.cart.api.dto.cart.AnonCartRefereceDTO;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.cart.config.MongoDBTestContainer;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.infra.repository.CartRepository;
import br.com.ecommerce.common.annotations.IdRolePair;
import br.com.ecommerce.common.annotations.TestCustomWithMockUser;
import br.com.ecommerce.common.utils.MockUserUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MongoDBTestContainer.class)
@AutoConfigureJsonTesters
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ClientCartControllerTest {

    final String basePath = "/carts";
    static Cart userCartPersisted = null;
    static Cart anonCartPersisted = null;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JacksonTester<AnonCartRefereceDTO> anonCartRefereceDTOJson;
    @Autowired
    private JacksonTester<UpdateCartProductDTO> updateCartProductDTOJson;

    @Autowired
    private CartRepository repository;

    @MockBean
    private ProductClient productClient;
    @MockBean
    private OrderClient orderClient;

    @BeforeEach
    void setup(@Autowired CartRepository repository) {
        Product product1 = new Product("1", 10);

        Cart cart1 = new Cart("userCartMerge");
        cart1.addProduct(product1);
        userCartPersisted = repository.save(cart1);

        Product product2 = new Product("100", 10);
        Cart cart2 = new Cart(product2);
        anonCartPersisted = repository.save(cart2);
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }


    @TestCustomWithMockUser(idRolePair = @IdRolePair(id = "createTest", role = "CLIENT"))
    void createTest01() throws Exception {
        // act
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("createTest"))
            .andExpect(jsonPath("$.products").hasJsonPath())
            .andExpect(jsonPath("$.totalPrice").hasJsonPath())
            .andExpect(jsonPath("$.createdAt").hasJsonPath())
            .andExpect(jsonPath("$.modifiedAt").hasJsonPath())
            .andExpect(jsonPath("$.anon").hasJsonPath());
    }

    @TestCustomWithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void createTest02_withUnauthorizedRoles() throws Exception {
        // act
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "CLIENT")
    void getTest01() throws Exception {
        // arrange
        String userId = userCartPersisted.getId();
        MockUserUtils.mockUser(userId);

        this.mockProductClientReturn(userCartPersisted.getProducts());

        // act
        ResultActions act = mvc.perform(get(basePath)
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").hasJsonPath())
            .andExpect(jsonPath("$.products[0]").hasJsonPath())
            .andExpect(jsonPath("$.products[0].id").hasJsonPath())
            .andExpect(jsonPath("$.products[0].name").hasJsonPath())
            .andExpect(jsonPath("$.products[0].unit").hasJsonPath())
            .andExpect(jsonPath("$.products[0].price").hasJsonPath())
            .andExpect(jsonPath("$.totalPrice").hasJsonPath())
            .andExpect(jsonPath("$.createdAt").hasJsonPath())
            .andExpect(jsonPath("$.modifiedAt").hasJsonPath())
            .andExpect(jsonPath("$.anon").hasJsonPath());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void getTest02_withUnauthorizedRoles() throws Exception {
        // act
        ResultActions act = mvc.perform(get(basePath)
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    void updateUnitTest01() throws Exception {
        // arrange
        String userId = userCartPersisted.getId();
        MockUserUtils.mockUser(userId);

        Product existentProduct = userCartPersisted.getProducts().iterator().next();
        String productId = existentProduct.getId();
        int productUnit = existentProduct.getUnit();
        System.out.println("UPDATE UNIT TEST");
        this.mockProductClientReturn(userCartPersisted.getProducts());
        
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO(productId, productUnit);
        int expectedUnit = productUnit * 2;
        
        // act
        ResultActions act = mvc.perform(put(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").hasJsonPath())
            .andExpect(jsonPath("$.products[0]").hasJsonPath())
            .andExpect(jsonPath("$.products[0].id").hasJsonPath())
            .andExpect(jsonPath("$.products[0].name").hasJsonPath())
            .andExpect(jsonPath("$.products[0].unit").hasJsonPath())
            .andExpect(jsonPath("$.products[0].price").hasJsonPath())
            .andExpect(jsonPath("$.totalPrice").hasJsonPath())
            .andExpect(jsonPath("$.createdAt").hasJsonPath())
            .andExpect(jsonPath("$.modifiedAt").hasJsonPath())
            .andExpect(jsonPath("$.anon").hasJsonPath());
        Product product = repository.findById(userCartPersisted.getId())
            .get().getProducts().stream()
            .filter(p -> p.getId().equalsIgnoreCase(productId))
            .findFirst().get();
        assertEquals(expectedUnit, product.getUnit());
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    void updateUnitTest02_withInvalidData() throws Exception {
        // arrange
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO();

        // act
        ResultActions act = mvc.perform(put(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void updateUnitTest03_withUnauthorizedRoles() throws Exception {
        // act
        ResultActions act = mvc.perform(put(basePath)
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    void mergeCartsTest01() throws Exception {
        // arrange
        String userId = userCartPersisted.getId();
        MockUserUtils.mockUser(userId);

        AnonCartRefereceDTO requestBody = new AnonCartRefereceDTO(anonCartPersisted.getId());

        Set<Product> copiedSet = new HashSet<>();
        copiedSet.addAll(userCartPersisted.getProducts());
        copiedSet.addAll(anonCartPersisted.getProducts());
        this.mockProductClientReturn(copiedSet);

        int expectedUserCartProductSetSize = userCartPersisted.getProducts().size() + anonCartPersisted.getProducts().size();
        
        // act
        ResultActions act = mvc.perform(put(basePath + "/merge")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", userId)
            .content(anonCartRefereceDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.products[0]").hasJsonPath())
            .andExpect(jsonPath("$.products[0].id").hasJsonPath())
            .andExpect(jsonPath("$.products[0].name").hasJsonPath())
            .andExpect(jsonPath("$.products[0].unit").hasJsonPath())
            .andExpect(jsonPath("$.products[0].price").hasJsonPath())
            .andExpect(jsonPath("$.totalPrice").hasJsonPath())
            .andExpect(jsonPath("$.createdAt").hasJsonPath())
            .andExpect(jsonPath("$.modifiedAt").hasJsonPath())
            .andExpect(jsonPath("$.anon").hasJsonPath());
        int userCartproductSetSize = repository.findById(userCartPersisted.getId()).get().getProducts().size();
        assertEquals(expectedUserCartProductSetSize, userCartproductSetSize);
        assertFalse(repository.existsById(anonCartPersisted.getId()));
    }

    @Test
    @WithMockUser(roles = {"CLIENT"})
    void mergeCartsTest02_withInvalidData() throws Exception {
        // arrange
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO();

        // act
        ResultActions act = mvc.perform(put(basePath + "/merge")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void mergeCartsTest03_withUnauthorizedRoles() throws Exception {
        // act
        ResultActions act = mvc.perform(put(basePath + "/merge")
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isForbidden());
    }

    private void mockProductClientReturn(Set<Product> products) {
        when(productClient.existsProduct(anyString()))
            .thenReturn(ResponseEntity.ok().build());

        Map<String, InternalProductDataDTO> mockProductResponse = products.stream()
            .collect(Collectors.toMap(
                Product::getId, 
                product -> new InternalProductDataDTO("Random Name", BigDecimal.TEN, "imageLink")));

        Set<String> productClientEntry = products.stream()
            .map(Product::getId)
            .collect(Collectors.toSet());
        when(productClient.getPrices(productClientEntry))
            .thenReturn(mockProductResponse);
    }
}