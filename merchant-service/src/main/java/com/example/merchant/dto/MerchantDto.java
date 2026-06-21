package com.example.merchant.dto;

import com.example.merchant.model.MerchantStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Merchant account details")
public class MerchantDto {

    @Schema(description = "Internal database ID", example = "1")
    private Long id;

    @Schema(description = "Unique merchant business identifier", example = "MERCH001")
    private String merchantId;

    @Schema(description = "Merchant business name", example = "Acme Corp")
    private String name;

    @Schema(description = "Business email address", example = "acme@example.com")
    private String email;

    @Schema(description = "Merchant API key (for authentication)", example = "mk_live_abc123")
    private String apiKey;

    @Schema(description = "Account status")
    private MerchantStatus status;

    @Schema(description = "Type of business", example = "RETAIL")
    private String businessType;

    @Schema(description = "Contact phone number", example = "+1-555-0100")
    private String contactPhone;

    @Schema(description = "Business address", example = "123 Main St, New York, NY 10001")
    private String address;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public MerchantDto() {}

    public Long getId()                   { return id; }
    public void setId(Long id)            { this.id = id; }

    public String getMerchantId()                 { return merchantId; }
    public void setMerchantId(String merchantId)  { this.merchantId = merchantId; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getApiKey()             { return apiKey; }
    public void setApiKey(String apiKey)  { this.apiKey = apiKey; }

    public MerchantStatus getStatus()             { return status; }
    public void setStatus(MerchantStatus status)  { this.status = status; }

    public String getBusinessType()                   { return businessType; }
    public void setBusinessType(String businessType)  { this.businessType = businessType; }

    public String getContactPhone()               { return contactPhone; }
    public void setContactPhone(String phone)     { this.contactPhone = phone; }

    public String getAddress()                { return address; }
    public void setAddress(String address)    { this.address = address; }

    public LocalDateTime getCreatedAt()               { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()               { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MerchantDto dto = new MerchantDto();

        public Builder id(Long v)               { dto.id = v; return this; }
        public Builder merchantId(String v)     { dto.merchantId = v; return this; }
        public Builder name(String v)           { dto.name = v; return this; }
        public Builder email(String v)          { dto.email = v; return this; }
        public Builder apiKey(String v)         { dto.apiKey = v; return this; }
        public Builder status(MerchantStatus v) { dto.status = v; return this; }
        public Builder businessType(String v)   { dto.businessType = v; return this; }
        public Builder contactPhone(String v)   { dto.contactPhone = v; return this; }
        public Builder address(String v)        { dto.address = v; return this; }
        public Builder createdAt(LocalDateTime v) { dto.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { dto.updatedAt = v; return this; }

        public MerchantDto build() { return dto; }
    }
}
