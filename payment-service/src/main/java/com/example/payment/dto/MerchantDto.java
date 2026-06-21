package com.example.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Merchant data retrieved from the Merchant Service")
public class MerchantDto {

    @Schema(description = "Merchant business identifier", example = "MERCH001")
    private String merchantId;

    @Schema(description = "Business name", example = "Acme Corp")
    private String name;

    @Schema(description = "Business email", example = "acme@example.com")
    private String email;

    @Schema(description = "Account status", example = "ACTIVE")
    private String status;

    @Schema(description = "Business type", example = "RETAIL")
    private String businessType;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    public MerchantDto() {}

    public String getMerchantId()                 { return merchantId; }
    public void setMerchantId(String merchantId)  { this.merchantId = merchantId; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getStatus()             { return status; }
    public void setStatus(String status)  { this.status = status; }

    public String getBusinessType()                   { return businessType; }
    public void setBusinessType(String businessType)  { this.businessType = businessType; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MerchantDto dto = new MerchantDto();

        public Builder merchantId(String v) { dto.merchantId = v; return this; }
        public Builder name(String v)       { dto.name = v; return this; }
        public Builder status(String v)     { dto.status = v; return this; }

        public MerchantDto build() { return dto; }
    }
}
