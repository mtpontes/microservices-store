package br.com.ecommerce.payment.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class RabbitMQTestContainerConfig {

    @Bean
    @DynamicPropertySource
    @SuppressWarnings("resource")
    public RabbitMQContainer getRabbitContainer(DynamicPropertyRegistry registry) {
        try (RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
                .withExposedPorts(5672, 15672)) {
            registry.add("spring.rabbitmq.port", () -> rabbit.getAmqpPort());

            return rabbit;
        }
    }
}