package br.com.ecommerce.cart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.cart.config.MongoDBTestContainer;

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoDBTestContainer.class)
class CartApplicationTests {

	@Test
	void contextLoads() {
	}
}