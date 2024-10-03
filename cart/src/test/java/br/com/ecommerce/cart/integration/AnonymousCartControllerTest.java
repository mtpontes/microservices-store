package br.com.ecommerce.cart.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import br.com.ecommerce.cart.api.client.OrderClient;
import br.com.ecommerce.cart.api.client.ProductClient;
import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.cart.config.MongoDBTestContainer;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.infra.repository.CartRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureJsonTesters
@Import(MongoDBTestContainer.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class AnonymousCartControllerTest {

    final String basePath = "/anonymous/carts";
    static Cart userCartPersisted = null;
    static Cart anonCartPersisted = null;

    @Autowired
    private MockMvc mvc;
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
        Product product2 = new Product("200", 20);
        
        Cart cart1 = new Cart("userCartMerge");
        cart1.addProduct(product1);
        cart1.addProduct(product2);
        userCartPersisted = repository.save(cart1);
        
        Product product3 = new Product("100", 10);
        Cart cart2 = new Cart(product3);
        anonCartPersisted = repository.save(cart2);
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void createTest01() throws Exception {
        // arrange
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO("newproduct", 1);
        this.mockProductClientReturn(Set.of(new Product(requestBody.getId(), requestBody.getUnit())));

        // act
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isOk())
            .andExpect(jsonPath("$.id").hasJsonPath())
            .andExpect(jsonPath("$.products").hasJsonPath())
            .andExpect(jsonPath("$.totalPrice").hasJsonPath())
            .andExpect(jsonPath("$.createdAt").hasJsonPath())
            .andExpect(jsonPath("$.modifiedAt").hasJsonPath())
            .andExpect(jsonPath("$.anon").hasJsonPath());
    }

    @Test
    void createTest02_withInvalidData() throws Exception {
        // arrange
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO();

        // act
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isBadRequest());
    }
    
    @Test
    void getTest01() throws Exception {
        // arrange
        String userId = userCartPersisted.getId();
        this.mockProductClientReturn(userCartPersisted.getProducts());

        // act
        ResultActions act = mvc.perform(get(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-anon-cart-id", userId));

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
    void getTest02_withoutHeader() throws Exception {
        // arrange
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO("newproduct", 1);

        // act
        ResultActions act = mvc.perform(get(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));
            // .header("X-anon-cart-id", userId));

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Test
    void updateUnitTest01_addingUnit() throws Exception {
        // arrange
        String header = userCartPersisted.getId();
        Product existentProduct = userCartPersisted.getProducts().iterator().next();
        String productId = existentProduct.getId();
        int productUnit = existentProduct.getUnit();
        this.mockProductClientReturn(userCartPersisted.getProducts());
        
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO(productId, productUnit);
        int expectedUnit = productUnit * 2;
        
        // act
        ResultActions act = mvc.perform(put(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-anon-cart-id", header)
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
    void updateUnitTest02_withInvalidData() throws Exception {
        // arrange
        UpdateCartProductDTO requestBody = new UpdateCartProductDTO();

        // act
        ResultActions act = mvc.perform(post(basePath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateCartProductDTOJson.write(requestBody).getJson()));

        // assert
        act.andExpect(status().isBadRequest());
    }

    @Test
    void updateUnitTest02_withUnauthorizedRoles() throws Exception {
        // act
        ResultActions act = mvc.perform(put(basePath)
            .contentType(MediaType.APPLICATION_JSON));

        // assert
        act.andExpect(status().isBadRequest());
    }

    private void mockProductClientReturn(Set<Product> products) {
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