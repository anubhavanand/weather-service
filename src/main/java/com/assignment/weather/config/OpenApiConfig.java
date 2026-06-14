package com.assignment.weather.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weatherOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Weather Metrics API")
                        .version("1.0")
                        .description("API for ingesting and querying weather sensor metrics"));
    }
}
