package br.com.ecommerce.orders.tools.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import br.com.ecommerce.orders.tools.utils.RandomUtils;

@TestConfiguration
public class TestConfigBeans {

    @Bean
    public RandomUtils getRandomUtils() {
        return new RandomUtils();
    }
}