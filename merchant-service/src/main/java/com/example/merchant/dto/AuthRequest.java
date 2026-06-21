package com.example.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for merchant authentication")
public class AuthRequest {

    @NotBlank(message = "Merchant ID is required")
    @Schema(description = "Unique merchant identifier", example = "MERCH001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String merchantId;

    @NotBlank(message = "API key is required")
    @Schema(description = "Merchant API key", example = "mk_live_abc123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apiKey;

    @NotBlank(message = "API secret is required")
    @Schema(description = "Merchant API secret", example = "ms_live_secret001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apiSecret;

    public AuthRequest() {}

    public AuthRequest(String merchantId, String apiKey, String apiSecret) {
        this.merchantId = merchantId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
}
