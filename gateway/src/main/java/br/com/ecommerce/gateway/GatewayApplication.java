package br.com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(r -> r.path("/api/v1/products/**")
				.filters(f -> f.stripPrefix(3))
				.uri("lb://products-ms"))

			.route(r -> r.path("/api/v1/accounts/**")
				.filters(f -> f.stripPrefix(3))
				.uri("lb://accounts-ms"))

			.route(r -> r.path("/api/v1/carts/**")
				.filters(f -> f.stripPrefix(3))
				.uri("lb://carts-ms"))

			.route(r -> r.path("/api/v1/orders/**")
				.filters(f -> f.stripPrefix(3))
				.uri("lb://orders-ms"))
				
			.route(r -> r.path("/api/v1/payments/**")
				.filters(f -> f.stripPrefix(3))
				.uri("lb://payments-ms"))
        .build();
	}
}