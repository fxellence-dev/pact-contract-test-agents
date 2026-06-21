package com.example.merchant.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Merchant Account Management Service API")
                        .description("""
                                Manages merchant accounts and issues JWT tokens used by the Payment Service.
                                
                                **Authentication Flow:**
                                1. Call `POST /api/merchants/auth/token` with your `merchantId`, `apiKey`, and `apiSecret`.
                                2. Use the returned JWT as `Authorization: Bearer <token>` on all protected endpoints.
                                
                                **Test Credentials (pre-seeded):**
                                | merchantId | apiKey          | apiSecret           |
                                |------------|-----------------|---------------------|
                                | MERCH001   | mk_live_abc123  | ms_live_secret001   |
                                | MERCH002   | mk_live_def456  | ms_live_secret002   |
                                | MERCH003   | mk_live_ghi789  | ms_live_secret003   |
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Payment Platform Team")
                                .email("platform@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development")))
                // Global security — all endpoints require BearerAuth EXCEPT those overridden
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtained from POST /api/merchants/auth/token")));
    }
}
