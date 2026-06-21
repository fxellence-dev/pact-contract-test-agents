package com.example.payment.dto;

import com.example.payment.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Payment transaction details")
public class PaymentResponse {

    @Schema(description = "Internal payment ID", example = "1")
    private Long id;

    @Schema(description = "Unique transaction reference", example = "txn_550e8400-e29b-41d4-a716-446655440000")
    private String transactionId;

    @Schema(description = "Merchant that processed this payment", example = "MERCH001")
    private String merchantId;

    @Schema(description = "Merchant business name", example = "Acme Corp")
    private String merchantName;

    @Schema(description = "Payment amount", example = "149.99")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Transaction status")
    private PaymentStatus status;

    @Schema(description = "Payment method used", example = "CARD")
    private String paymentMethod;

    @Schema(description = "Payer name", example = "John Doe")
    private String payerName;

    @Schema(description = "Payer email", example = "john.doe@example.com")
    private String payerEmail;

    @Schema(description = "Payment description", example = "Order #12345")
    private String description;

    @Schema(description = "Processor reference (e.g. card network auth code)", example = "AUTH_abc123")
    private String processorReference;

    @Schema(description = "Transaction creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public PaymentResponse() {}

    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }

    public String getTransactionId()                      { return transactionId; }
    public void setTransactionId(String transactionId)    { this.transactionId = transactionId; }

    public String getMerchantId()                 { return merchantId; }
    public void setMerchantId(String merchantId)  { this.merchantId = merchantId; }

    public String getMerchantName()                   { return merchantName; }
    public void setMerchantName(String merchantName)  { this.merchantName = merchantName; }

    public BigDecimal getAmount()             { return amount; }
    public void setAmount(BigDecimal amount)  { this.amount = amount; }

    public String getCurrency()               { return currency; }
    public void setCurrency(String currency)  { this.currency = currency; }

    public PaymentStatus getStatus()          { return status; }
    public void setStatus(PaymentStatus s)    { this.status = s; }

    public String getPaymentMethod()                  { return paymentMethod; }
    public void setPaymentMethod(String method)       { this.paymentMethod = method; }

    public String getPayerName()              { return payerName; }
    public void setPayerName(String name)     { this.payerName = name; }

    public String getPayerEmail()             { return payerEmail; }
    public void setPayerEmail(String email)   { this.payerEmail = email; }

    public String getDescription()                { return description; }
    public void setDescription(String d)          { this.description = d; }

    public String getProcessorReference()             { return processorReference; }
    public void setProcessorReference(String ref)     { this.processorReference = ref; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime t)         { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()               { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)         { this.updatedAt = t; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PaymentResponse r = new PaymentResponse();

        public Builder id(Long v)                       { r.id = v; return this; }
        public Builder transactionId(String v)          { r.transactionId = v; return this; }
        public Builder merchantId(String v)             { r.merchantId = v; return this; }
        public Builder merchantName(String v)           { r.merchantName = v; return this; }
        public Builder amount(BigDecimal v)             { r.amount = v; return this; }
        public Builder currency(String v)               { r.currency = v; return this; }
        public Builder status(PaymentStatus v)          { r.status = v; return this; }
        public Builder paymentMethod(String v)          { r.paymentMethod = v; return this; }
        public Builder payerName(String v)              { r.payerName = v; return this; }
        public Builder payerEmail(String v)             { r.payerEmail = v; return this; }
        public Builder description(String v)            { r.description = v; return this; }
        public Builder processorReference(String v)     { r.processorReference = v; return this; }
        public Builder createdAt(LocalDateTime v)       { r.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v)       { r.updatedAt = v; return this; }

        public PaymentResponse build() { return r; }
    }
}
