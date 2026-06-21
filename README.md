# Payment Platform — Spring Boot Microservices

Two Spring Boot 3.2 microservices demonstrating JWT-secured inter-service communication with H2 in-memory databases.

---

## Architecture

```
┌────────────────────────────────────────────────────────────────┐
│  Client                                                         │
│                                                                 │
│  1. POST /api/merchants/auth/token  ──────────────────────────►│
│     { merchantId, apiKey, apiSecret }        Merchant Service   │
│                                              (port 8081)        │
│  ◄─────────────────── { token: "eyJ..." } ──────────────────── │
│                                                                 │
│  2. POST /api/payments                                          │
│     Authorization: Bearer eyJ...  ─────────────────────────── ►│
│     { amount, currency, ... }                Payment Service    │
│                                              (port 8082)        │
│                     3. GET /api/merchants/{merchantId}          │
│                        Authorization: Bearer eyJ...  ─────────►│
│                                              Merchant Service   │
│                     ◄─── { merchantId, name, status, ... } ─── │
│                                                                 │
│  ◄────────────────── { transactionId, status, ... } ─────────  │
└────────────────────────────────────────────────────────────────┘
```

---

## Services

| Service | Port | Swagger UI | H2 Console |
|---------|------|-----------|------------|
| **merchant-service** | 8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/h2-console |
| **payment-service**  | 8082 | http://localhost:8082/swagger-ui.html | http://localhost:8082/h2-console |

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Merchant Service
```bash
cd merchant-service
mvn spring-boot:run
```

### Run Payment Service (separate terminal)
```bash
cd payment-service
mvn spring-boot:run
```

---

## JWT Token Flow

### Step 1 — Authenticate and get JWT (public endpoint)
```bash
curl -s -X POST http://localhost:8081/api/merchants/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": "MERCH001",
    "apiKey":     "mk_live_abc123",
    "apiSecret":  "ms_live_secret001"
  }' | jq .
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "merchantId": "MERCH001",
  "merchantName": "Acme Corp"
}
```

### Step 2 — Use JWT for protected Merchant endpoints
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# List all merchants
curl -s http://localhost:8081/api/merchants \
  -H "Authorization: Bearer $TOKEN" | jq .

# Get specific merchant
curl -s http://localhost:8081/api/merchants/MERCH001 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Step 3 — Process a payment
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -s -X POST http://localhost:8082/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount":        149.99,
    "currency":      "USD",
    "paymentMethod": "CARD",
    "payerName":     "John Doe",
    "payerEmail":    "john.doe@example.com",
    "description":   "Order #12345"
  }' | jq .
```

Response:
```json
{
  "transactionId": "txn_550e8400-e29b-41d4-a716-446655440000",
  "merchantId":    "MERCH001",
  "merchantName":  "Acme Corp",
  "amount":        149.99,
  "currency":      "USD",
  "status":        "COMPLETED",
  "processorReference": "AUTH_A1B2C3D4",
  ...
}
```

### Step 4 — Query payments
```bash
# List all payments for the authenticated merchant
curl -s http://localhost:8082/api/payments \
  -H "Authorization: Bearer $TOKEN" | jq .

# Get specific payment
curl -s http://localhost:8082/api/payments/txn_550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## Pre-seeded Test Credentials

| merchantId | apiKey          | apiSecret           | Name         |
|------------|-----------------|---------------------|--------------|
| MERCH001   | mk_live_abc123  | ms_live_secret001   | Acme Corp    |
| MERCH002   | mk_live_def456  | ms_live_secret002   | TechStore Inc|
| MERCH003   | mk_live_ghi789  | ms_live_secret003   | Fashion Hub  |

---

## Security Model

| Endpoint | Service | Auth Required |
|----------|---------|---------------|
| `POST /api/merchants/auth/token` | merchant-service | **No** — public |
| `GET /api/merchants` | merchant-service | **Yes** — JWT |
| `GET /api/merchants/{id}` | merchant-service | **Yes** — JWT |
| `POST /api/merchants` | merchant-service | **Yes** — JWT |
| `PUT /api/merchants/{id}` | merchant-service | **Yes** — JWT |
| `DELETE /api/merchants/{id}` | merchant-service | **Yes** — JWT |
| `POST /api/payments` | payment-service | **Yes** — JWT |
| `GET /api/payments` | payment-service | **Yes** — JWT |
| `GET /api/payments/{txnId}` | payment-service | **Yes** — JWT |
| `PATCH /api/payments/{txnId}/cancel` | payment-service | **Yes** — JWT |

JWT claims include `merchantId` and `merchantName`. Both services share the **same signing secret** (configured in `application.yml`).

---

## OpenAPI Specifications

Static YAML specs are in the `openapi/` directory:

| File | Description |
|------|-------------|
| [openapi/merchant-service-openapi.yml](openapi/merchant-service-openapi.yml) | Merchant Service contract |
| [openapi/payment-service-openapi.yml](openapi/payment-service-openapi.yml) | Payment Service contract |

Live Swagger UIs (when running):
- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html

---

## Project Structure

```
.
├── merchant-service/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/merchant/
│       │   ├── MerchantServiceApplication.java
│       │   ├── DataInitializer.java           ← seeds test merchants on startup
│       │   ├── config/
│       │   │   ├── SecurityConfig.java        ← JWT filter, public auth endpoint
│       │   │   └── OpenApiConfig.java
│       │   ├── security/
│       │   │   ├── JwtUtil.java               ← generates & validates JWTs
│       │   │   └── JwtAuthenticationFilter.java
│       │   ├── model/
│       │   │   ├── Merchant.java
│       │   │   └── MerchantStatus.java
│       │   ├── repository/MerchantRepository.java
│       │   ├── dto/
│       │   │   ├── AuthRequest.java
│       │   │   ├── AuthResponse.java
│       │   │   ├── MerchantDto.java
│       │   │   ├── MerchantCreateRequest.java
│       │   │   ├── MerchantUpdateRequest.java
│       │   │   └── ErrorResponse.java
│       │   ├── service/MerchantService.java
│       │   ├── controller/MerchantController.java
│       │   └── exception/
│       │       ├── GlobalExceptionHandler.java
│       │       ├── MerchantNotFoundException.java
│       │       └── UnauthorizedException.java
│       └── test/java/com/example/merchant/pact/
│           └── MerchantServicePactProviderTest.java  ← Pact provider verification
├── payment-service/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/payment/
│       │   ├── PaymentServiceApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java        ← all endpoints require JWT
│       │   │   ├── OpenApiConfig.java
│       │   │   └── RestTemplateConfig.java
│       │   ├── security/
│       │   │   ├── JwtUtil.java               ← validates JWTs (same secret as merchant-service)
│       │   │   └── JwtAuthenticationFilter.java
│       │   ├── model/
│       │   │   ├── Payment.java
│       │   │   └── PaymentStatus.java
│       │   ├── repository/PaymentRepository.java
│       │   ├── dto/
│       │   │   ├── PaymentRequest.java
│       │   │   ├── PaymentResponse.java
│       │   │   ├── MerchantDto.java
│       │   │   └── ErrorResponse.java
│       │   ├── service/PaymentService.java
│       │   ├── client/MerchantClient.java     ← calls merchant-service with forwarded JWT
│       │   ├── controller/PaymentController.java
│       │   └── exception/
│       │       ├── GlobalExceptionHandler.java
│       │       ├── PaymentNotFoundException.java
│       │       └── MerchantServiceException.java
│       └── test/java/com/example/payment/pact/
│           └── MerchantClientPactTest.java    ← Pact consumer tests
├── pact-broker/
│   └── docker-compose.yml                    ← self-hosted Pact Broker + Postgres
└── openapi/
    ├── merchant-service-openapi.yml
    └── payment-service-openapi.yml
```

---

## Configuration Notes

### JWT Secret
Both services use the **same** `jwt.secret` in their `application.yml`. The value is a Base64-encoded string representing a 256-bit HMAC-SHA256 signing key.

> **Never commit production secrets to source control.** Use environment variables or a secrets manager in production:
> ```bash
> JWT_SECRET=<base64-encoded-key> mvn spring-boot:run
> ```

### Merchant Service URL
The Payment Service calls the Merchant Service at the URL configured by:
```yaml
merchant:
  service:
    url: http://localhost:8081
```
Change this when deploying to Kubernetes or other environments.

---

## Contract Testing with Pact

This project uses **[Pact](https://docs.pact.io)** consumer-driven contract testing to verify the inter-service integration between `payment-service` (consumer) and `merchant-service` (provider) without running both services simultaneously.

### Why contract testing?

The Payment Service calls one endpoint on the Merchant Service:
```
GET /api/merchants/{merchantId}
Authorization: Bearer <forwarded-JWT>
```
If the Merchant Service team renames the `status` field, changes the error response shape, or returns `403` instead of `401`, the Payment Service breaks at runtime. Pact catches these breaks automatically — before deployment.

### How it works

```
payment-service                  Pact Broker               merchant-service
      │                         (localhost:9292)                  │
      │                                                           │
      │  1. Consumer tests run                                    │
      │     MerchantClient called against Pact mock server        │
      │     Pact records every request + expected response        │
      │                                                           │
      │  2. Pact file published ──────────────────────────────►  │
      │     payment-service-merchant-service.json                 │
      │                                                           │
      │                         3. Provider test pulls pact ──────│
      │                            Replays each interaction       │
      │                            against the real               │
      │                            merchant-service               │
      │                                                           │
      │                         4. Results published ─────────────│
      │                                                           │
      │  5. can-i-deploy ──────►│◄─── can-i-deploy ──────────────│
      │     "safe to deploy"         "safe to deploy"             │
```

### Interactions covered (5 total)

| # | Scenario | Request | Expected response |
|---|---|---|---|
| 1 | Merchant is ACTIVE | `GET /api/merchants/MERCH001` + valid JWT | `200` `{ status: "ACTIVE" }` |
| 2 | Merchant is INACTIVE | `GET /api/merchants/MERCH001` + valid JWT | `200` `{ status: "INACTIVE" }` |
| 3 | Merchant is SUSPENDED | `GET /api/merchants/MERCH001` + valid JWT | `200` `{ status: "SUSPENDED" }` |
| 4 | Merchant not found | `GET /api/merchants/MERCH999` + valid JWT | `404` with error body |
| 5 | Invalid JWT | `GET /api/merchants/MERCH001` + `Bearer invalid-token` | `401` with error body |

> **Bug caught by contract testing:** The Merchant Service was returning `403` instead of `401` for requests with an invalid JWT. The provider verification test surfaced this immediately, and `SecurityConfig.java` was fixed to return the correct `401` with a JSON body.

---

### Prerequisites

- Docker + Docker Compose (for the Pact Broker)
- Java 17+ and Maven 3.8+

---

### Step 1 — Start the Pact Broker

```bash
cd pact-broker
docker compose up -d
```

The broker UI is available at **http://localhost:9292** (login: `admin` / `admin`).

---

### Step 2 — Run the consumer tests (payment-service)

Generates the pact file in `payment-service/target/pacts/`.

```bash
cd payment-service
mvn test -Dtest=MerchantClientPactTest
```

---

### Step 3 — Publish the pact to the broker

```bash
cd /path/to/project/root

docker run --rm --network host \
  -v $(pwd)/payment-service/target/pacts:/pacts \
  pactfoundation/pact-cli:latest \
  broker publish /pacts \
  --consumer-app-version 1.0.0 \
  --branch main \
  --broker-base-url http://localhost:9292 \
  --broker-username admin \
  --broker-password admin
```

---

### Step 4 — Run the provider verification (merchant-service)

Pulls the pact from the broker and replays all 5 interactions against the real Merchant Service.

```bash
cd merchant-service
mvn test -Dtest=MerchantServicePactProviderTest
```

---

### Step 5 — Publish verification results

```bash
cd merchant-service
mvn test -Dtest=MerchantServicePactProviderTest \
  -Dpact.verifier.publishResults=true \
  -Dpact.provider.version=1.0.0 \
  -Dpact.provider.branch=main
```

---

### Step 6 — Check can-i-deploy

Run this before deploying either service to confirm the contract matrix is green.

```bash
# Is payment-service safe to deploy?
docker run --rm --network host pactfoundation/pact-cli:latest \
  broker can-i-deploy \
  --pacticipant payment-service --version 1.0.0 \
  --to-environment production \
  --broker-base-url http://localhost:9292 \
  --broker-username admin --broker-password admin

# Is merchant-service safe to deploy?
docker run --rm --network host pactfoundation/pact-cli:latest \
  broker can-i-deploy \
  --pacticipant merchant-service --version 1.0.0 \
  --to-environment production \
  --broker-base-url http://localhost:9292 \
  --broker-username admin --broker-password admin
```

Both should respond: `Computer says yes ✅`

---

### Key files

| File | Role |
|---|---|
| `payment-service/src/test/.../MerchantClientPactTest.java` | Consumer test — defines the 5 interactions |
| `payment-service/target/pacts/payment-service-merchant-service.json` | Generated pact file (gitignored) |
| `merchant-service/src/test/.../MerchantServicePactProviderTest.java` | Provider test — verifies against real service |
| `pact-broker/docker-compose.yml` | Self-hosted Pact Broker + Postgres |
