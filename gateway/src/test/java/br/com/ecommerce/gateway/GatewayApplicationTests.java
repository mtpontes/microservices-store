package br.com.ecommerce.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "api.security.gateway.name=gateway")
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}
}