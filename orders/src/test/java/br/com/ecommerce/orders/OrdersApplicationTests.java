package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import br.com.ecommerce.orders.testcontainers.RabbitMQTestContainerConfig;


@SpringBootTest
@TestPropertySource(properties = {"eureka.client.enabled=false", "eureka.client.register-with-eureka=false", "eureka.client.fetch-registry=false"})
@AutoConfigureTestDatabase
@Import({RabbitMQTestContainerConfig.class})
class OrdersApplicationTests {

	@Test
	void contextLoads() {
	}
}