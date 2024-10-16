package br.com.ecommerce.products.unit.business.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecommerce.products.api.dto.product.CompletePriceDataDTO;
import br.com.ecommerce.products.api.dto.product.DataStockDTO;
import br.com.ecommerce.products.api.dto.product.StockWriteOffDTO;
import br.com.ecommerce.products.api.dto.product.UpdatePriceDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductPriceResponseDTO;
import br.com.ecommerce.products.api.dto.product.UpdateProductResponseDTO;
import br.com.ecommerce.products.api.mapper.PriceMapper;
import br.com.ecommerce.products.api.mapper.ProductMapper;
import br.com.ecommerce.products.api.mapper.StockMapper;
import br.com.ecommerce.products.api.mapper.factory.ProductDTOFactory;
import br.com.ecommerce.products.business.service.PriceJobService;
import br.com.ecommerce.products.business.service.ProductService;
import br.com.ecommerce.products.business.validator.UniqueNameProductValidator;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;
import br.com.ecommerce.products.infra.exception.exceptions.ProductNotFoundException;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.infra.repository.ProductRepository;
import br.com.ecommerce.products.utils.builder.ProductTestBuilder;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    private final Product defaultProduct = new ProductTestBuilder()
        .name("name")
        .build();

    @Mock
    private ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ManufacturerRepository manufacturerRepository;

    @Mock
    private ProductDTOFactory dtoFactory;

    @Mock
    private PriceMapper priceMapper;
    @Mock
    private StockMapper stockMapper;
    @Mock
    private ProductMapper productMapper;

    @Mock
    private UniqueNameProductValidator uniqueNameValidator;

    @Mock
    private PriceJobService priceScheduler;

    @InjectMocks
    private ProductService service;

    @Captor
    private ArgumentCaptor<Product> productCaptor;


    @Test
    @DisplayName("Unit - getProduct - Must not return product details by non-existent ID")
    void getProductTest() {
        assertThrows(ProductNotFoundException.class, () -> service.getProduct(10L));
    }

    @Test
    @DisplayName("Unit - updateProductData - Update product with full data")
    void updateProductDataTest01() {
        // arrange
        Product target = this.defaultProduct;
        
        UpdateProductDTO requestBody = new UpdateProductDTO(
            "UPDATE-NAME", null, null);

        when(repository.findById(any()))
            .thenReturn(Optional.of(target));

        when(repository.save(any()))
            .thenReturn(target);

        UpdateProductResponseDTO response = new UpdateProductResponseDTO(
            null, requestBody.getName(), null, null);
        when(productMapper.toProductUpdateResponseDTO(eq(target)))
            .thenReturn(response);
        
        // act
        UpdateProductResponseDTO result = service.updateProductData(1L, requestBody);
        
        // assert
        assertEquals(requestBody.getName(), result.getName());
        verify(uniqueNameValidator).validate(eq(requestBody.getName()));
        
        verify(productMapper)
            .toProductUpdateResponseDTO(productCaptor.capture());
        Product productUpdated = productCaptor.getValue();
        assertEquals(requestBody.getName(), productUpdated.getName());
    }

    @Test
    @DisplayName("Unit - updateProductData - Should throw exception when not finding Product")
    void updateProductDataTest02() {
        // arrange
        UpdateProductDTO requestBody = new UpdateProductDTO(null, null, null);
        assertThrows(
            ProductNotFoundException.class, 
            () -> service.updateProductData(1L, requestBody));

        verify(uniqueNameValidator)
            .validate(eq(requestBody.getName()));
    }

    @Test
    @DisplayName("Unit - updateProductPrice - Must update product price by product ID")
    void updateProductPriceTest() {
        // arrange
        Price price = new Price(BigDecimal.valueOf(50), BigDecimal.valueOf(25));
        Product product = new ProductTestBuilder()
            .price(price)
            .build();

        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(product));

        Price newPrice = new Price(BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        UpdatePriceDTO requestBody = new UpdatePriceDTO(
            newPrice.getOriginalPrice(), newPrice.getPromotionalPrice());

        when(priceMapper.toPrice(eq(requestBody)))
            .thenReturn(newPrice);

        when(repository.save(eq(product)))
            .thenReturn(product);

        UpdateProductPriceResponseDTO response = new UpdateProductPriceResponseDTO();  
        when(productMapper.toUpdateProductPriceResponseDTO(eq(product), any()))
            .thenReturn(response);

        // act
        service.updateProductPrice(1L, requestBody);

        // assert
        verify(productMapper)
            .toUpdateProductPriceResponseDTO(productCaptor.capture(), any());
        Price result = productCaptor.getValue().getPrice();

        assertEquals(requestBody.getOriginalPrice(), result.getOriginalPrice());
        assertEquals(requestBody.getOriginalPrice(), result.getCurrentPrice());
    }

    @Test
    @DisplayName("Unit - updateProductPrice - Must update product price by product ID")
    void switchCurrentPriceToOriginalPriceTest() {
        // arrange
        Price price = new Price(BigDecimal.valueOf(50), BigDecimal.valueOf(25));
        Product product = new ProductTestBuilder()
            .price(price)
            .build();

        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(product));

        when(repository.save(eq(product)))
            .thenReturn(product);

        Price productPrice = product.getPrice();
;
        var response = new UpdateProductPriceResponseDTO(
            product.getId(),
            product.getName(),
            new CompletePriceDataDTO(
                productPrice.getCurrentPrice(),
                productPrice.getOriginalPrice(),
                productPrice.getPromotionalPrice(),
                productPrice.isOnPromotion(),
                productPrice.getEndOfPromotion())
        );
        when(productMapper.toUpdateProductPriceResponseDTO(eq(product), any()))
            .thenReturn(response);

        // act
        service.switchCurrentPriceToOriginal(1L);

        // assert
        verify(productMapper)
            .toUpdateProductPriceResponseDTO(productCaptor.capture(), any());
        Price result = productCaptor.getValue().getPrice();

        assertEquals(price.getOriginalPrice(), result.getCurrentPrice());
    }

    @Test
    @DisplayName("Unit - updateProductPrice - Must update product price by product ID")
    void switchCurrentPriceToPromotionalPriceTest() {
        // arrange
        Price price = new Price(BigDecimal.valueOf(50), BigDecimal.valueOf(25));
        Product product = new ProductTestBuilder()
            .price(price)
            .build();
        LocalDateTime endOfPromotion = LocalDateTime.now().plusDays(1);

        when(repository.findById(anyLong()))
            .thenReturn(Optional.of(product));

        when(repository.save(eq(product)))
            .thenReturn(product);

        Price productPrice = product.getPrice();
        var response = new UpdateProductPriceResponseDTO(
            product.getId(),
            product.getName(),
            new CompletePriceDataDTO(
                productPrice.getCurrentPrice(),
                productPrice.getOriginalPrice(),
                productPrice.getPromotionalPrice(),
                productPrice.isOnPromotion(),
                productPrice.getEndOfPromotion())
        );
        when(productMapper.toUpdateProductPriceResponseDTO(eq(product), any()))
            .thenReturn(response);

        // act
        service.switchCurrentPriceToPromotional(1L, endOfPromotion);

        // assert
        verify(productMapper)
            .toUpdateProductPriceResponseDTO(productCaptor.capture(), any());
        Price result = productCaptor.getValue().getPrice();

        assertEquals(price.getPromotionalPrice(), result.getCurrentPrice());
    }

    @Test
    @DisplayName("Unit - updateStockByProductId - Must update product stock by product ID")
    void updateStockByProductIdTest01() {
        // arrange
        Product product = new ProductTestBuilder()
            .stock(new Stock(100))            
            .build();

        when(repository.getReferenceById(anyLong()))
            .thenReturn(product);

        // act and assert
        assertAll(
            () -> {
                service.updateStockByProductId(1L, new DataStockDTO(-100));
                assertEquals(0, product.getStock().getUnit());
            },
            
            () -> {
                service.updateStockByProductId(1L, new DataStockDTO(+150));
                assertEquals(+150, product.getStock().getUnit());
            },
            
            () -> {
                service.updateStockByProductId(1L, new DataStockDTO(-200));
                assertEquals(0, product.getStock().getUnit());
            }
        );
    }

    @Test
    @DisplayName("Unit - updateStocks - Must update stocks of multiple products")
    void updateStocksTes01() {
        // arrange
        List<Product> products = List.of(
            new ProductTestBuilder().id(1L).stock(new Stock(100)).build(),
            new ProductTestBuilder().id(2L).stock(new Stock(200)).build(),
            new ProductTestBuilder().id(3L).stock(new Stock(300)).build()
        );

        List<StockWriteOffDTO> stockWriteOff = List.of(
            new StockWriteOffDTO(1L, 100),
            new StockWriteOffDTO(2L, 200),
            new StockWriteOffDTO(3L, 300)
        );

        when(repository.findAllById(anyList()))
            .thenReturn(products);

        // act
        service.updateStocks(stockWriteOff);

        // assert
        assertAll(
            () -> assertEquals(0, products.get(0).getStock().getUnit()),
            () -> assertEquals(0, products.get(1).getStock().getUnit()),
            () -> assertEquals(0, products.get(2).getStock().getUnit())
        );
    }
}