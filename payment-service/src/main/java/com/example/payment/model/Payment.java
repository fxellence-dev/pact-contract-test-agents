package com.example.payment.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    /** Merchant that submitted this payment — extracted from JWT claim */
    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String payerName;
    private String payerEmail;
    private String description;

    /** e.g. CARD, BANK_TRANSFER, WALLET */
    private String paymentMethod;

    /** Optional card/wallet reference returned after processing */
    private String processorReference;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Payment() {}

    // ── Getters ────────────────────────────────────────────────────────────
    public Long getId()                       { return id; }
    public String getTransactionId()          { return transactionId; }
    public String getMerchantId()             { return merchantId; }
    public BigDecimal getAmount()             { return amount; }
    public String getCurrency()               { return currency; }
    public PaymentStatus getStatus()          { return status; }
    public String getPayerName()              { return payerName; }
    public String getPayerEmail()             { return payerEmail; }
    public String getDescription()            { return description; }
    public String getPaymentMethod()          { return paymentMethod; }
    public String getProcessorReference()     { return processorReference; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public LocalDateTime getUpdatedAt()       { return updatedAt; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setId(Long id)                           { this.id = id; }
    public void setTransactionId(String transactionId)   { this.transactionId = transactionId; }
    public void setMerchantId(String merchantId)         { this.merchantId = merchantId; }
    public void setAmount(BigDecimal amount)             { this.amount = amount; }
    public void setCurrency(String currency)             { this.currency = currency; }
    public void setStatus(PaymentStatus status)          { this.status = status; }
    public void setPayerName(String payerName)           { this.payerName = payerName; }
    public void setPayerEmail(String payerEmail)         { this.payerEmail = payerEmail; }
    public void setDescription(String description)       { this.description = description; }
    public void setPaymentMethod(String paymentMethod)   { this.paymentMethod = paymentMethod; }
    public void setProcessorReference(String ref)        { this.processorReference = ref; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
