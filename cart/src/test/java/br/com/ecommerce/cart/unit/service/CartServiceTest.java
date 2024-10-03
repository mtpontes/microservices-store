package br.com.ecommerce.cart.unit.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.cart.api.dto.cart.UpdateCartProductDTO;
import br.com.ecommerce.cart.api.mapper.CartMapper;
import br.com.ecommerce.cart.api.mapper.ProductMapper;
import br.com.ecommerce.cart.infra.entity.Cart;
import br.com.ecommerce.cart.infra.entity.Product;
import br.com.ecommerce.cart.infra.entity.factory.CartFactory;
import br.com.ecommerce.cart.infra.entity.factory.ProductFactory;
import br.com.ecommerce.cart.infra.exception.exceptions.CartNotFoundException;
import br.com.ecommerce.cart.infra.repository.CartRepository;
import br.com.ecommerce.cart.service.CartService;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartFactory cartFactory;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CartMapper cartMapper;
    
    @InjectMocks
    private CartService service;

    @Captor
    private ArgumentCaptor<Cart> cartCaptor;

    private final Random random = new Random();
    private final ProductFactory productFactory = new ProductFactory();

    @BeforeEach
    void setup() {
        service = new CartService(
            cartRepository, 
            new CartFactory(), 
            new ProductMapper(productFactory), 
            new CartMapper());
    }


    @Test
    void createCartTest() {
        // arrange
        String entry = "validId";

        // act
        var result = service.createCart(entry);

        // assert
        assertEquals(entry, result.getId());
        assertTrue(result.getProducts().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
        assertFalse(result.isAnon());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getModifiedAt());

        verify(cartRepository).save(any());
    }

    @Test
    void createAnonCartTest() {
        // arrange
        UpdateCartProductDTO entry = new UpdateCartProductDTO("1", 10);
        int expectedProductsSetSize = 1;

        // act
        service.createAnonCart(entry);
        verify(cartRepository).save(cartCaptor.capture());
        Cart result = cartCaptor.getValue();

        // assert
        assertNotNull(result.getId());
        assertEquals(expectedProductsSetSize, result.getProducts().size());
        assertTrue(result.isAnon());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getModifiedAt());
    }

    @Test
    void getCartTest_whenNotFoundCart() {
        assertThrows(
            CartNotFoundException.class, 
            () -> service.getCart("any id"), 
            "Throws exception if not found a Cart");
    }

    @Test
    void getUserCartTest01_whenNotFoundCart() {
        Cart anonCart = new Cart();
        ReflectionTestUtils.setField(anonCart, "isAnon", true);
        System.out.println("VALOR: " + anonCart);

        when(cartRepository.findById(anyString()))
            .thenReturn(Optional.of(anonCart));

        assertThrows(
            CartNotFoundException.class, 
            () -> service.getUserCart("any id"), 
            "Throws exception if not found a Cart");
    }

    @Test
    void getUserCartTest02_whenNotFoundCart() {
        Cart anonCart = new Cart();
        ReflectionTestUtils.setField(anonCart, "isAnon", true);
        System.out.println("VALOR: " + anonCart);

        when(cartRepository.findById(anyString()))
            .thenReturn(Optional.of(anonCart));

        assertThrows(
            CartNotFoundException.class, 
            () -> service.getUserCart("any id"), 
            "Throws exception if not found a Cart");
    }

    @Test
    void mergeCartTest_whenNotFoundCart() {
        assertThrows(
            CartNotFoundException.class, 
            () -> service.getCart("any id"),
            "Throws exception if not found a Cart");

        when(cartRepository.findById(anyString()))
            .thenReturn(Optional.of(new Cart()));
        assertDoesNotThrow(
            () -> service.getCart("any id"),
            "Does not throw exception when found a Cart");
    }

    @Test
    void changeProductUnitTest01_addNonexistenteProduct() {
        // arrange
        String userId = "validid";
        Cart cart = new Cart(userId);

        when(cartRepository.findById(eq(userId)))
            .thenReturn(Optional.of(cart));
        when(cartRepository.save(eq(cart)))
            .thenReturn(cart);

        String productId = "productId";
        int productUnit = 100;
        UpdateCartProductDTO entry = new UpdateCartProductDTO(productId, productUnit);

        // act
        service.changeProductUnit(userId, entry);
        var result = cart.getProducts().iterator().next();

        // assert
        System.out.println("AQUI : " + cart.getProducts());
        assertEquals(entry.getId(), result.getId());
        assertEquals(entry.getUnit(), result.getUnit());
    }

    @Test
    void changeProductUnitTest02_increateProductUnit() {
        // arrange
        String userId = "validid";
        Cart cart = new Cart(userId);
        Product product = productFactory.createProduct("id", 100);
        cart.addProduct(product);

        when(cartRepository.findById(eq(userId)))
            .thenReturn(Optional.of(cart));

        when(cartRepository.save(eq(cart)))
            .thenReturn(cart);

        UpdateCartProductDTO entry = new UpdateCartProductDTO(product.getId(), Math.abs(product.getUnit()));
        int expectedProductUnit = product.getUnit() + product.getUnit();

        // act
        service.changeProductUnit(userId, entry);
        var result = cart.getProducts().iterator().next();

        // assert
        assertEquals(expectedProductUnit, result.getUnit());
    }

    @Test
    void changeProductUnitTest03_removeProduct() {
        // arrange
        String userId = "validid";
        Cart cart = new Cart(userId);
        Product product = productFactory.createProduct("id", 100);
        cart.addProduct(product);

        when(cartRepository.findById(eq(userId)))
            .thenReturn(Optional.of(cart));

        when(cartRepository.save(eq(cart)))
            .thenReturn(cart);

        UpdateCartProductDTO entry = new UpdateCartProductDTO(product.getId(), Math.negateExact(product.getUnit()));

        // act
        service.changeProductUnit(userId, entry);
        var result = cart.getProducts();

        // assert
        assertTrue(result.isEmpty());
    }

    @Test
    void changeProductUnitTest_whenNotFoundCart() {
        assertThrows(
            CartNotFoundException.class,
            () -> service.changeProductUnit("any id", new UpdateCartProductDTO()),
            "Does not throw exception when found a Cart");
    }

    @Test
    void selectProductsFromCartTest() {
        // arrange
        Cart cart = new Cart("any id");
        cart.addProducts(this.createProductSet());
        String chosenProduct = cart.getProducts().iterator().next().getId();
        int expectedProductSetSize = 1;

        // act
        Set<Product> products = service.selectProductsFromCart(cart, Set.of(chosenProduct));

        // assert
        assertEquals(expectedProductSetSize, products.size());
        assertEquals(products.iterator().next().getUnit(), cart.getProducts().iterator().next().getUnit());
    }

    private Set<Product> createProductSet() {
        return IntStream.range(0, 3)
            .mapToObj(p -> {
                Product product = new Product();
                ReflectionTestUtils.setField(product, "id", String.valueOf(random.nextInt(1, 10) + p));
                ReflectionTestUtils.setField(product, "unit", 1);
                return product;
            }).collect(Collectors.toSet());
    }
}