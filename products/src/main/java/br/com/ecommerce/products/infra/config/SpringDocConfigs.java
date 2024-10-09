package br.com.ecommerce.products.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition
public class SpringDocConfigs {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-key", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .info(new Info()
                .title("Product Service")
                .description("This is the product microservice from the Microservices Store API")
                .contact(new Contact()
                    .name("Mateus Pontes")));
    }
}