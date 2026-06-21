package com.example.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Payment submission request")
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 4, message = "Amount must have at most 13 integer digits and 4 decimal places")
    @Schema(description = "Payment amount", example = "149.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO 4217 code")
    @Schema(description = "ISO 4217 currency code", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CARD",
            allowableValues = {"CARD", "BANK_TRANSFER", "WALLET"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentMethod;

    @Schema(description = "Payer full name", example = "John Doe")
    private String payerName;

    @Email(message = "Invalid payer email format")
    @Schema(description = "Payer email address", example = "john.doe@example.com")
    private String payerEmail;

    @Size(max = 255)
    @Schema(description = "Payment description / memo", example = "Order #12345 — 2x Widget")
    private String description;

    public PaymentRequest() {}

    public BigDecimal getAmount()             { return amount; }
    public void setAmount(BigDecimal amount)  { this.amount = amount; }

    public String getCurrency()               { return currency; }
    public void setCurrency(String currency)  { this.currency = currency; }

    public String getPaymentMethod()                  { return paymentMethod; }
    public void setPaymentMethod(String method)       { this.paymentMethod = method; }

    public String getPayerName()              { return payerName; }
    public void setPayerName(String name)     { this.payerName = name; }

    public String getPayerEmail()             { return payerEmail; }
    public void setPayerEmail(String email)   { this.payerEmail = email; }

    public String getDescription()                { return description; }
    public void setDescription(String description){ this.description = description; }
}
