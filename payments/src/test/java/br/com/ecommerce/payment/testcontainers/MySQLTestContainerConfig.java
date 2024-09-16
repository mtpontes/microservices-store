package br.com.ecommerce.payment.testcontainers;

import javax.sql.DataSource;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class MySQLTestContainerConfig {

    @Bean
    @SuppressWarnings("resource")
    public MySQLContainer<?> mysqlContainer() {
        try (MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.36")
                .withDatabaseName("testdb")
                .withUsername("root")
                .withPassword("root")) {
            mysqlContainer.start();
            return mysqlContainer;
        }
    }

    @Primary
    @Bean
    public DataSource dataSource(MySQLContainer<?> mysqlContainer) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(mysqlContainer.getDriverClassName());
        dataSource.setUrl(mysqlContainer.getJdbcUrl());
        dataSource.setUsername(mysqlContainer.getUsername());
        dataSource.setPassword(mysqlContainer.getPassword());
        return dataSource;
    }
}