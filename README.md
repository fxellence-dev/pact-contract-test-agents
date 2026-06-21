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
│   └── src/main/java/com/example/merchant/
│       ├── MerchantServiceApplication.java
│       ├── DataInitializer.java           ← seeds test merchants on startup
│       ├── config/
│       │   ├── SecurityConfig.java        ← JWT filter, public auth endpoint
│       │   └── OpenApiConfig.java
│       ├── security/
│       │   ├── JwtUtil.java               ← generates & validates JWTs
│       │   └── JwtAuthenticationFilter.java
│       ├── model/
│       │   ├── Merchant.java
│       │   └── MerchantStatus.java
│       ├── repository/MerchantRepository.java
│       ├── dto/
│       │   ├── AuthRequest.java
│       │   ├── AuthResponse.java
│       │   ├── MerchantDto.java
│       │   ├── MerchantCreateRequest.java
│       │   ├── MerchantUpdateRequest.java
│       │   └── ErrorResponse.java
│       ├── service/MerchantService.java
│       ├── controller/MerchantController.java
│       └── exception/
│           ├── GlobalExceptionHandler.java
│           ├── MerchantNotFoundException.java
│           └── UnauthorizedException.java
├── payment-service/
│   ├── pom.xml
│   └── src/main/java/com/example/payment/
│       ├── PaymentServiceApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java        ← all endpoints require JWT
│       │   ├── OpenApiConfig.java
│       │   └── RestTemplateConfig.java
│       ├── security/
│       │   ├── JwtUtil.java               ← validates JWTs (same secret as merchant-service)
│       │   └── JwtAuthenticationFilter.java
│       ├── model/
│       │   ├── Payment.java
│       │   └── PaymentStatus.java
│       ├── repository/PaymentRepository.java
│       ├── dto/
│       │   ├── PaymentRequest.java
│       │   ├── PaymentResponse.java
│       │   ├── MerchantDto.java
│       │   └── ErrorResponse.java
│       ├── service/PaymentService.java
│       ├── client/MerchantClient.java     ← calls merchant-service with forwarded JWT
│       ├── controller/PaymentController.java
│       └── exception/
│           ├── GlobalExceptionHandler.java
│           ├── PaymentNotFoundException.java
│           └── MerchantServiceException.java
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
