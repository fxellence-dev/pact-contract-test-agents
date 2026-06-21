package com.example.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response")
public class AuthResponse {

    @Schema(description = "JWT bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token validity in seconds", example = "86400")
    private Long expiresIn;

    @Schema(description = "Authenticated merchant ID", example = "MERCH001")
    private String merchantId;

    @Schema(description = "Authenticated merchant name", example = "Acme Corp")
    private String merchantName;

    public AuthResponse() {}

    public AuthResponse(String token, String tokenType, Long expiresIn, String merchantId, String merchantName) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }

    public String getToken()        { return token; }
    public void setToken(String t)  { this.token = t; }

    public String getTokenType()        { return tokenType; }
    public void setTokenType(String t)  { this.tokenType = t; }

    public Long getExpiresIn()          { return expiresIn; }
    public void setExpiresIn(Long e)    { this.expiresIn = e; }

    public String getMerchantId()           { return merchantId; }
    public void setMerchantId(String id)    { this.merchantId = id; }

    public String getMerchantName()             { return merchantName; }
    public void setMerchantName(String name)    { this.merchantName = name; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token, tokenType, merchantId, merchantName;
        private Long expiresIn;

        public Builder token(String v)        { this.token = v; return this; }
        public Builder tokenType(String v)    { this.tokenType = v; return this; }
        public Builder expiresIn(Long v)      { this.expiresIn = v; return this; }
        public Builder merchantId(String v)   { this.merchantId = v; return this; }
        public Builder merchantName(String v) { this.merchantName = v; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, tokenType, expiresIn, merchantId, merchantName);
        }
    }
}
