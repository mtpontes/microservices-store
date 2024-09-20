package br.com.ecommerce.orders.tools.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework. context.annotation. Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class MongoDBTestContainer {

    private static final int CONTAINER_PORT = 27017;

    @Bean
    public GenericContainer<?> getMongoContainer(DynamicPropertyRegistry registry) {
        var mongo = new GenericContainer<>("mongo:8");
        mongo.addExposedPort(CONTAINER_PORT);
        mongo.addEnv("MONGO_INITDB_ROOT_USERNAME", "root");
        mongo.addEnv("MONGO_INITDB_ROOT_PASSWORD", "root");

        mongo.start();
        registry.add("spring.data.mongodb.port", () -> mongo.getMappedPort(CONTAINER_PORT));
        return mongo;
    }
}