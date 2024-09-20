package br.com.ecommerce.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import br.com.ecommerce.orders.tools.testcontainers.RabbitMQTestContainerConfig;


@SpringBootTest
@TestPropertySource(properties = {
	"eureka.client.enabled=false", 
	"eureka.client.register-with-eureka=false", 
	"eureka.client.fetch-registry=false",
	"api.security.ips.allowed=localhost,127.0.0.1"})
@Import({RabbitMQTestContainerConfig.class})
class OrdersApplicationTests {

	@Test
	void contextLoads() {
	}
}