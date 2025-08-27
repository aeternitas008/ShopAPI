package com.example.shop.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shop API")
                        .version("1.0")
                        .description("API для управления магазином")
                        .contact(new Contact()
                                .name("Артур")
                                .url("https://github.com/aeternitas008")
                                .email("mmisericordiamt@gmail.com")));
    }

    @Bean
    public OpenApiCustomizer globalResponsesCustomizer() {
        return openApi -> openApi.getPaths().values()
                .forEach(pathItem -> pathItem.readOperations().forEach(this::addGlobalResponses));
    }

    private void addGlobalResponses(Operation operation) {
        ApiResponses responses = operation.getResponses();

        if (!responses.containsKey("400")) {
            responses.addApiResponse("400",
                    new ApiResponse().description("Некорректный запрос/ Неверный ID сущности"));
        }
        if (!responses.containsKey("500")) {
            responses.addApiResponse("500",
                    new ApiResponse().description("Внутренняя ошибка сервера"));
        }
    }
}
