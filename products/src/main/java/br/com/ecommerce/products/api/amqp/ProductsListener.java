package br.com.ecommerce.products.api.amqp;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.api.dto.product.StockWriteOffDTO;
import br.com.ecommerce.products.business.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ProductsListener {

	private final ProductService service;


	@RabbitListener(queues = "products.stock-orders")
	public void receiveQueueMessageOrder(@Payload @Valid List<StockWriteOffDTO> dto) {
		service.updateStocks(dto);
	}
}