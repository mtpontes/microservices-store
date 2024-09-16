package br.com.ecommerce.products;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import br.com.ecommerce.products.testcontainers.RabbitMQTestContainerConfig;

@SpringBootTest
@TestPropertySource(properties = {
	"eureka.client.enabled=false", 
	"eureka.client.register-with-eureka=false", 
	"eureka.client.fetch-registry=false",
	"api.security.ips.allowed=localhost,127.0.0.1"})
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {RabbitMQTestContainerConfig.class})
class ProductsApplicationTests {

	@Test
	void contextLoads() {
	}
}