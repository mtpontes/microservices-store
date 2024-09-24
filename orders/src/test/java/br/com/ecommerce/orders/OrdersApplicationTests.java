package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.orders.tools.testcontainers.RabbitMQTestContainerConfig;


@SpringBootTest
@ActiveProfiles("test")
@Import({RabbitMQTestContainerConfig.class})
class OrdersApplicationTests {

	@Test
	void contextLoads() {
	}
}