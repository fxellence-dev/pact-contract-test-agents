package com.example.merchant.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String apiKey;

    /** BCrypt-hashed API secret — never expose in responses */
    @Column(nullable = false)
    private String apiSecret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status;

    private String businessType;
    private String contactPhone;
    private String address;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Merchant() {}

    // ── Getters ────────────────────────────────────────────────────────────
    public Long getId()               { return id; }
    public String getMerchantId()     { return merchantId; }
    public String getName()           { return name; }
    public String getEmail()          { return email; }
    public String getApiKey()         { return apiKey; }
    public String getApiSecret()      { return apiSecret; }
    public MerchantStatus getStatus() { return status; }
    public String getBusinessType()   { return businessType; }
    public String getContactPhone()   { return contactPhone; }
    public String getAddress()        { return address; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setId(Long id)                       { this.id = id; }
    public void setMerchantId(String merchantId)     { this.merchantId = merchantId; }
    public void setName(String name)                 { this.name = name; }
    public void setEmail(String email)               { this.email = email; }
    public void setApiKey(String apiKey)             { this.apiKey = apiKey; }
    public void setApiSecret(String apiSecret)       { this.apiSecret = apiSecret; }
    public void setStatus(MerchantStatus status)     { this.status = status; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public void setContactPhone(String phone)        { this.contactPhone = phone; }
    public void setAddress(String address)           { this.address = address; }

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
