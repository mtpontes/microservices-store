package br.com.ecommerce.orders.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecommerce.orders.api.client.ProductClient;
import br.com.ecommerce.orders.api.dto.order.OrderDTO;
import br.com.ecommerce.orders.api.dto.product.InternalProductDataDTO;
import br.com.ecommerce.orders.api.dto.product.ProductAndUnitDTO;
import br.com.ecommerce.orders.api.dto.product.ProductDTO;
import br.com.ecommerce.orders.api.dto.product.ProductOutOfStockDTO;
import br.com.ecommerce.orders.api.mapper.OrderMapper;
import br.com.ecommerce.orders.api.mapper.ProductMapper;
import br.com.ecommerce.orders.business.service.OrderService;
import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import br.com.ecommerce.orders.infra.exception.exceptions.OrderNotFoundException;
import br.com.ecommerce.orders.infra.exception.exceptions.OutOfStockException;
import br.com.ecommerce.orders.infra.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

	@Mock
	private Order orderMock;
	@Mock
	private OrderRepository repository;
	@Mock
	private ProductClient productClient;
	@Mock
	private OrderMapper orderMapper;
	@Mock
	private ProductMapper productMapper;
	@InjectMocks
	private OrderService service;

	@Captor
	private ArgumentCaptor<Order> orderCaptor;

	private final BigDecimal price = BigDecimal.ONE;


	@Test
	@DisplayName("Unit - saveOrder - Should throw an exception when validating that the stock is insufficient")
	void saveOrderValidateProductsStocksTest01() {
		// arrange
		var input = Set.of(
			new ProductAndUnitDTO("1", 100),
			new ProductAndUnitDTO("2", 100),
			new ProductAndUnitDTO("3", 100)
		);

		var responseBodyVerifyStocks = Set.of(
			new ProductOutOfStockDTO("1", "product-1", 1), 
			new ProductOutOfStockDTO("2", "product-2", 1), 
			new ProductOutOfStockDTO("3", "product-3", 1)
		);
		when(productClient.verifyStocks(any()))
			.thenReturn(responseBodyVerifyStocks);

		// act and assert
		assertThrows(OutOfStockException.class, 
			() -> service.saveOrder(input, "1"));
	}

	@Test
	@DisplayName("Unit - saveOrder - Must create product successfully")
	void saveOrder01() {
		// arrange
		var input = Set.of(
			new ProductAndUnitDTO("1", 100),
			new ProductAndUnitDTO("2", 100)
		);

		// simulates that all products have sufficient stock to create the order
		Set<ProductOutOfStockDTO> responseBodyVerifyStocks = Set.of();
		when(productClient.verifyStocks(any()))
			.thenReturn(responseBodyVerifyStocks);

        Set<String> listOfIds = input.stream().map(ProductAndUnitDTO::getId).collect(Collectors.toSet());
        InternalProductDataDTO nameAndPrice = new InternalProductDataDTO("any name", BigDecimal.ONE);
        Map<String, InternalProductDataDTO> priceMap = listOfIds.stream()
            .collect(Collectors.toMap(id -> id, id -> nameAndPrice));
        when(productClient.getPrices(eq(listOfIds)))
            .thenReturn(priceMap);

		// act
		service.saveOrder(input, "1");
		verify(repository).save(orderCaptor.capture());

		// assert
		var result = orderCaptor.getValue();
		assertEquals("1", result.getUserId());
		assertEquals(200, result.getTotal().intValue());
		assertEquals(OrderStatus.AWAITING_PAYMENT, result.getStatus());
		assertEquals(2, result.getProducts().size());
	}

	@Test
	@DisplayName("Unit - getOrderById - Should throw exception when not finding the product by ID")
	void getOrderByIdTest01() {
		when(repository.findByIdAndUserId(anyString(), anyString()))
			.thenReturn(Optional.empty());

		assertThrows(OrderNotFoundException.class, 
			() -> service.getOrderById("1", "1"));
	}

	@Test
    @DisplayName("Unit - getOrderById - Checks if the status update was made")
	void updateOrderStatusTest01() {
		// arrange
		when(repository.findById(anyString()))
			.thenReturn(Optional.of(orderMock));
		when(repository.save(orderMock))
			.thenReturn(orderMock);
	
		ProductDTO mockProductDTO = new ProductDTO("1", null, 1, price);
		OrderDTO mockOrderDTO = new OrderDTO(
            "1", 
            "1", 
            List.of(mockProductDTO), 
            BigDecimal.ZERO, 
            OrderStatus.CANCELED, 
            LocalDate.now());
		when(orderMapper.toOrderDTO(any(Order.class), anyList()))
			.thenReturn(mockOrderDTO);
	
		// act
		service.updateOrderStatus("1", OrderStatus.CANCELED);
	
		verify(orderMock).updateOrderStatus(any());
		verify(orderMapper).toOrderDTO(orderCaptor.capture(), anyList());
	}
}