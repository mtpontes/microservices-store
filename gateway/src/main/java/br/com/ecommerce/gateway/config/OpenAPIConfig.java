package br.com.ecommerce.gateway.config;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() throws UnknownHostException {        
        return new OpenAPI()
            .info(new Info().title("Microsservice API").version("v1"))
            .servers(Arrays.asList(
                new Server().url("http://localhost:9092").description("Local Server")
            ));
    }
}