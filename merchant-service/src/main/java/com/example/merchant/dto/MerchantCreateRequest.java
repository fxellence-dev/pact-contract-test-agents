package com.example.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request payload to create a new merchant account")
public class MerchantCreateRequest {

    @NotBlank(message = "Merchant ID is required")
    @Pattern(regexp = "^[A-Z0-9_-]{4,20}$", message = "Merchant ID must be 4-20 uppercase alphanumeric characters")
    @Schema(description = "Unique merchant identifier", example = "MERCH004", requiredMode = Schema.RequiredMode.REQUIRED)
    private String merchantId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    @Schema(description = "Business name", example = "New Merchant Ltd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Business email", example = "merchant@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "API key is required")
    @Schema(description = "API key for authentication", example = "mk_live_newkey123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apiKey;

    @NotBlank(message = "API secret is required")
    @Size(min = 8, message = "API secret must be at least 8 characters")
    @Schema(description = "API secret (will be hashed)", example = "ms_live_newsecret", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apiSecret;

    @Schema(description = "Business type", example = "RETAIL")
    private String businessType;

    @Schema(description = "Contact phone", example = "+1-555-0200")
    private String contactPhone;

    @Schema(description = "Business address", example = "456 Commerce Ave, Chicago, IL 60601")
    private String address;

    public MerchantCreateRequest() {}

    public String getMerchantId()                 { return merchantId; }
    public void setMerchantId(String merchantId)  { this.merchantId = merchantId; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getApiKey()             { return apiKey; }
    public void setApiKey(String apiKey)  { this.apiKey = apiKey; }

    public String getApiSecret()                  { return apiSecret; }
    public void setApiSecret(String apiSecret)    { this.apiSecret = apiSecret; }

    public String getBusinessType()                   { return businessType; }
    public void setBusinessType(String businessType)  { this.businessType = businessType; }

    public String getContactPhone()               { return contactPhone; }
    public void setContactPhone(String phone)     { this.contactPhone = phone; }

    public String getAddress()                { return address; }
    public void setAddress(String address)    { this.address = address; }
}
