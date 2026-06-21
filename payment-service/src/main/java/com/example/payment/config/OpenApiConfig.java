package com.example.payment.config;

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
                        .title("Payment Acceptance & Processing Service API")
                        .description("""
                                Processes payments on behalf of authenticated merchants.
                                
                                **Authentication Flow:**
                                1. Obtain a JWT from the Merchant Service: `POST http://localhost:8081/api/merchants/auth/token`
                                2. Include the token in every request: `Authorization: Bearer <token>`
                                3. The service extracts the `merchantId` from the JWT and automatically associates
                                   the payment with that merchant — no merchantId in the request body is needed.
                                
                                **Inter-service call:**
                                After validating the JWT, the Payment Service calls the Merchant Service
                                (`GET /api/merchants/{merchantId}`) using the same Bearer token to verify
                                the merchant account is still active before processing the payment.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Payment Platform Team")
                                .email("platform@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Development")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtained from Merchant Service POST /api/merchants/auth/token")));
    }
}
