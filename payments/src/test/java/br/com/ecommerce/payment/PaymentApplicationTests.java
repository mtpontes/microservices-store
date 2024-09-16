package br.com.ecommerce.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import br.com.ecommerce.payment.testcontainers.RabbitMQTestContainerConfig;

@SpringBootTest
@TestPropertySource(properties = {"eureka.client.enabled=false", "eureka.client.register-with-eureka=false", "eureka.client.fetch-registry=false"})
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {RabbitMQTestContainerConfig.class})
class PaymentApplicationTests {

	@Test
	void contextLoads() {
	}
}