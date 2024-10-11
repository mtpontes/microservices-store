package br.com.ecommerce.cart.infra.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@OpenAPIDefinition
public class SpringDocConfig {

    @Value("${server.port}")
    String port;
    
    @Value("${container.machine.host}")
    private String gatewayHost;


    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-key", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .info(new Info()
                .title("Cart Service")
                .description("This is the cart microservice from the Microservices Store API")
                .contact(new Contact()
                    .name("Mateus Pontes")))
            .servers(List.of(new Server().url("http://localhost:"+ port).description("Local Server")))
            .servers(List.of(new Server().url(String.format("http://%s:9092/api/v1/carts", gatewayHost)).description("Gateway Server")));
    }
}