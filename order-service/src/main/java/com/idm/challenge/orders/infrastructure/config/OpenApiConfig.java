package com.idm.challenge.orders.infrastructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Order Service API")
                        .version("v1")
                        .description("Reactive order service aligned with the hexagonal architecture.")
                        .contact(new Contact().name("IDM Challenge").email("tech.challenge@idm.com"))
                        .license(new License().name("Apache 2.0")));
    }

    @Bean
    public GroupedOpenApi orderApiGroup() {
        return GroupedOpenApi.builder()
                .group("orders")
                .pathsToMatch("/api/orders/**")
                .build();
    }
}
