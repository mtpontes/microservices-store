package br.com.ecommerce.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import br.com.ecommerce.payment.testcontainers.RabbitMQTestContainerConfig;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {RabbitMQTestContainerConfig.class})
class PaymentApplicationTests {

	@Test
	void contextLoads() {
	}
}