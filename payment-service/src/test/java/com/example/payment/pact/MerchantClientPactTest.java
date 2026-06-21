package com.example.payment.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.payment.client.MerchantClient;
import com.example.payment.dto.MerchantDto;
import com.example.payment.exception.MerchantServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pact consumer tests for the Payment Service → Merchant Service integration.
 *
 * Consumer: payment-service
 * Provider: merchant-service
 *
 * Covers the single endpoint the Payment Service calls:
 *   GET /api/merchants/{merchantId}
 *
 * Pact files are written to target/pacts/ and must be published to the Pact Broker
 * before provider verification can run.
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "merchant-service", pactVersion = PactSpecVersion.V3)
class MerchantClientPactTest {

    private static final String VALID_TOKEN   = "Bearer test-jwt-token";
    private static final String INVALID_TOKEN = "Bearer invalid-token";

    // -------------------------------------------------------------------------
    // Interaction 1 — Happy path: merchant is ACTIVE
    // -------------------------------------------------------------------------

    @Pact(consumer = "payment-service")
    RequestResponsePact activeMerchantPact(PactDslWithProvider builder) {
        return builder
            .given("a merchant with id MERCH001 exists and is ACTIVE")
            .uponReceiving("GET /api/merchants/MERCH001 with a valid JWT")
                .method("GET")
                .path("/api/merchants/MERCH001")
                .matchHeader("Authorization", "Bearer .+", VALID_TOKEN)
            .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                    body.stringValue("merchantId", "MERCH001");
                    body.stringType("name", "Acme Corp");
                    body.stringValue("status", "ACTIVE");
                }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "activeMerchantPact")
    void getMerchant_returnsActiveMerchant(MockServer mockServer) {
        MerchantDto merchant = clientFor(mockServer).getMerchant("MERCH001", VALID_TOKEN);

        assertThat(merchant.getMerchantId()).isEqualTo("MERCH001");
        assertThat(merchant.getStatus()).isEqualTo("ACTIVE");
        assertThat(merchant.getName()).isNotBlank();
    }

    // -------------------------------------------------------------------------
    // Interaction 2 — Merchant account is INACTIVE
    // The Payment Service reads the status field and rejects with 422 upstream.
    // This interaction verifies the client correctly deserialises non-ACTIVE statuses.
    // -------------------------------------------------------------------------

    @Pact(consumer = "payment-service")
    RequestResponsePact inactiveMerchantPact(PactDslWithProvider builder) {
        return builder
            .given("a merchant with id MERCH001 exists and is INACTIVE")
            .uponReceiving("GET /api/merchants/MERCH001 — merchant is INACTIVE")
                .method("GET")
                .path("/api/merchants/MERCH001")
                .matchHeader("Authorization", "Bearer .+", VALID_TOKEN)
            .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                    body.stringValue("merchantId", "MERCH001");
                    body.stringType("name", "Acme Corp");
                    body.stringValue("status", "INACTIVE");
                }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "inactiveMerchantPact")
    void getMerchant_returnsInactiveMerchant(MockServer mockServer) {
        MerchantDto merchant = clientFor(mockServer).getMerchant("MERCH001", VALID_TOKEN);

        assertThat(merchant.getStatus()).isEqualTo("INACTIVE");
    }

    // -------------------------------------------------------------------------
    // Interaction 3 — Merchant account is SUSPENDED
    // -------------------------------------------------------------------------

    @Pact(consumer = "payment-service")
    RequestResponsePact suspendedMerchantPact(PactDslWithProvider builder) {
        return builder
            .given("a merchant with id MERCH001 exists and is SUSPENDED")
            .uponReceiving("GET /api/merchants/MERCH001 — merchant is SUSPENDED")
                .method("GET")
                .path("/api/merchants/MERCH001")
                .matchHeader("Authorization", "Bearer .+", VALID_TOKEN)
            .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                    body.stringValue("merchantId", "MERCH001");
                    body.stringType("name", "Acme Corp");
                    body.stringValue("status", "SUSPENDED");
                }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "suspendedMerchantPact")
    void getMerchant_returnsSuspendedMerchant(MockServer mockServer) {
        MerchantDto merchant = clientFor(mockServer).getMerchant("MERCH001", VALID_TOKEN);

        assertThat(merchant.getStatus()).isEqualTo("SUSPENDED");
    }

    // -------------------------------------------------------------------------
    // Interaction 4 — Merchant not found (404)
    // MerchantClient maps this to MerchantServiceException; PaymentService → 502.
    // -------------------------------------------------------------------------

    @Pact(consumer = "payment-service")
    RequestResponsePact merchantNotFoundPact(PactDslWithProvider builder) {
        return builder
            .given("merchant MERCH999 does not exist")
            .uponReceiving("GET /api/merchants/MERCH999 — merchant not found")
                .method("GET")
                .path("/api/merchants/MERCH999")
                .matchHeader("Authorization", "Bearer .+", VALID_TOKEN)
            .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                    body.numberType("status", 404);
                    body.stringType("error", "Not Found");
                    body.stringType("message", "Merchant not found: MERCH999");
                }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "merchantNotFoundPact")
    void getMerchant_throwsMerchantServiceException_whenNotFound(MockServer mockServer) {
        assertThatThrownBy(() -> clientFor(mockServer).getMerchant("MERCH999", VALID_TOKEN))
            .isInstanceOf(MerchantServiceException.class)
            .hasMessageContaining("Merchant not found");
    }

    // -------------------------------------------------------------------------
    // Interaction 5 — Invalid / expired JWT (401)
    // MerchantClient maps this to MerchantServiceException; PaymentService → 502.
    // -------------------------------------------------------------------------

    @Pact(consumer = "payment-service")
    RequestResponsePact unauthorizedPact(PactDslWithProvider builder) {
        return builder
            .given("the request carries an invalid JWT")
            .uponReceiving("GET /api/merchants/MERCH001 with an invalid JWT")
                .method("GET")
                .path("/api/merchants/MERCH001")
                .matchHeader("Authorization", "Bearer .+", INVALID_TOKEN)
            .willRespondWith()
                .status(401)
                .headers(Map.of("Content-Type", "application/json"))
                .body(LambdaDsl.newJsonBody(body -> {
                    body.numberType("status", 401);
                    body.stringType("error", "Unauthorized");
                    body.stringType("message", "Full authentication is required to access this resource");
                }).build())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "unauthorizedPact")
    void getMerchant_throwsMerchantServiceException_whenUnauthorized(MockServer mockServer) {
        assertThatThrownBy(() -> clientFor(mockServer).getMerchant("MERCH001", INVALID_TOKEN))
            .isInstanceOf(MerchantServiceException.class)
            .hasMessageContaining("Unauthorized");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MerchantClient clientFor(MockServer mockServer) {
        MerchantClient client = new MerchantClient(new RestTemplate());
        setMerchantServiceUrl(client, mockServer.getUrl());
        return client;
    }

    private void setMerchantServiceUrl(MerchantClient client, String url) {
        try {
            Field field = MerchantClient.class.getDeclaredField("merchantServiceUrl");
            field.setAccessible(true);
            field.set(client, url);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to inject mock server URL into MerchantClient", e);
        }
    }
}
