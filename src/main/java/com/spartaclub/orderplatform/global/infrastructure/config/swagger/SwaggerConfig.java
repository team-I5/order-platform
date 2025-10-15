package com.spartaclub.orderplatform.global.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan(basePackages = {"com.spartaclub.orderplatform"})
@OpenAPIDefinition(
        info = @Info(
                title = "OrderPlatform",
                description = "OrderPlatform api 명세서",
                version = "1.0.0"
        )
)
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 개발 서버")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList("accessToken")
                        .addList("refreshToken"))
                .components(new Components()
                        .addSecuritySchemes("accessToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Access Token"))
                        .addSecuritySchemes("refreshToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Refresh-Token")
                                        .description("JWT RefreshToken Header")));
    }
}