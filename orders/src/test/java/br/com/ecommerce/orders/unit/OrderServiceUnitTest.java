package br.com.ecommerce.orders.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.ecommerce.orders.dto.order.OrderDTO;
import br.com.ecommerce.orders.dto.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.dto.product.ProductDTO;
import br.com.ecommerce.orders.exception.OutOfStockException;
import br.com.ecommerce.orders.exception.ProductOutOfStockDTO;
import br.com.ecommerce.orders.http.ProductClient;
import br.com.ecommerce.orders.mapper.OrderMapper;
import br.com.ecommerce.orders.mapper.ProductMapper;
import br.com.ecommerce.orders.model.Order;
import br.com.ecommerce.orders.model.OrderStatus;
import br.com.ecommerce.orders.repository.OrderRepository;
import br.com.ecommerce.orders.service.OrderService;
import jakarta.persistence.EntityNotFoundException;

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


	@Test
	@DisplayName("Unit - saveOrder - Should throw an exception when validating that the stock is insufficient")
	void saveOrderValidateProductsStocksTest01() {
		// arrange
		var input = List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100),
			new ProductDTO(3L, 100)
		);

		var responseBodyVerifyStocks = List.of(
			new ProductOutOfStockDTO(1L, "product-1", 1), 
			new ProductOutOfStockDTO(2L, "product-2", 1), 
			new ProductOutOfStockDTO(3L, "product-3", 1)
		);
		
		var responseMock = ResponseEntity
			.status(HttpStatus.MULTI_STATUS)
			.body(responseBodyVerifyStocks);
		when(productClient.verifyStocks(any())).thenReturn(responseMock);

		// act and assert
		assertThrows(OutOfStockException.class, 
			() -> service.saveOrder(input, 1L));
	}	
	@Test
	@DisplayName("Unit - saveOrder - Should throw exception when stock service response is different than 200")
	void saveOrderValidateProductsStocksTest02() {
		// arrange
		var input = List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100),
			new ProductDTO(3L, 100)
		);

		List<ProductOutOfStockDTO> responseBodyVerifyStocks = List.of();
		var responseMock = ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(responseBodyVerifyStocks);
		when(productClient.verifyStocks(any())).thenReturn(responseMock);

		// act and assert
		assertThrows(RuntimeException.class, 
			() -> service.saveOrder(input, 1L));
	}
	@Test
	@DisplayName("Unit - saveOrder - should throw exception when price service response is different than 200")
	void saveOrdergetPricedProducts01() {
		// arrange
		var input = List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100),
			new ProductDTO(3L, 100)
		);

		List<ProductOutOfStockDTO> responseBodyGetPrices = List.of();
		var responseMock = ResponseEntity
			.status(HttpStatus.OK)
			.body(responseBodyGetPrices);
		when(productClient.verifyStocks(any())).thenReturn(responseMock);

		when(productClient.getPrices(any()))
			.thenReturn(
				ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

		// act and assert
		assertThrows(RuntimeException.class, 
			() -> service.saveOrder(input, 1L));
	}
	@Test
	@DisplayName("Unit - saveOrder - Must create product successfully")
	void saveOrder01() {
		// arrange
		var input = List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100)
		);

		// simulates that all products have sufficient stock to create the order
		List<ProductOutOfStockDTO> responseBodyVerifyStocks = List.of();
		var responseVerifyStocks = ResponseEntity
			.status(HttpStatus.OK)
			.body(responseBodyVerifyStocks);
		when(productClient.verifyStocks(any()))
			.thenReturn(responseVerifyStocks);

		// simulates the recovery of product prices
		Set<ProductAndPriceDTO> responseBodyGetPrices = Set.of(
			new ProductAndPriceDTO(1L, BigDecimal.ONE),
			new ProductAndPriceDTO(2L, BigDecimal.ONE));
		var responseGetPrices = ResponseEntity
			.status(HttpStatus.OK)
			.body(responseBodyGetPrices);
		when(productClient.getPrices(any()))
			.thenReturn(responseGetPrices);
			
		// act
		service.saveOrder(input, 1L);
		verify(repository).save(orderCaptor.capture());

		// assert
		var result = orderCaptor.getValue();
		assertEquals(Long.valueOf(1L), result.getUserId());
		assertEquals(200, result.getTotal().intValue());
		assertEquals(OrderStatus.AWAITING_PAYMENT, result.getStatus());
		assertEquals(2, result.getProducts().size());
	}

	@Test
	@DisplayName("Unit - getOrderById - Should throw exception when not finding the product by ID")
	void getOrderByIdTest01() {
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
			.thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, 
			() -> service.getOrderById(1L, 1L));
	}

	@Test
	void updateOrderStatusTest01() {
		// arrange
		when(repository.findById(anyLong()))
			.thenReturn(Optional.of(orderMock));
		when(repository.save(orderMock))
			.thenReturn(orderMock);
	
		ProductDTO mockProductDTO = new ProductDTO(1L, 1);
		OrderDTO mockOrderDTO = new OrderDTO(1L, 1L, List.of(mockProductDTO), BigDecimal.ZERO, LocalDate.now(), OrderStatus.CANCELED); // Substitua pelos valores apropriados
		when(orderMapper.toOrderDTO(any(Order.class), anyList()))
			.thenReturn(mockOrderDTO);
	
		// act
		service.updateOrderStatus(1L, OrderStatus.CANCELED);
	
		// verify the orderMapper call with appropriate matchers
		verify(orderMock).updateOrderStatus(any());
		verify(orderMapper).toOrderDTO(orderCaptor.capture(), anyList());
	}
}