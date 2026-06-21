package com.example.merchant.dto;

import com.example.merchant.model.MerchantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload to update a merchant account (only provided fields are updated)")
public class MerchantUpdateRequest {

    @Size(min = 2, max = 100)
    @Schema(description = "New business name", example = "Acme Corp Updated")
    private String name;

    @Email(message = "Invalid email format")
    @Schema(description = "New business email", example = "newemail@example.com")
    private String email;

    @Schema(description = "New account status")
    private MerchantStatus status;

    @Schema(description = "New business type", example = "WHOLESALE")
    private String businessType;

    @Schema(description = "New contact phone", example = "+1-555-9999")
    private String contactPhone;

    @Schema(description = "New business address", example = "789 New Street, Dallas, TX 75201")
    private String address;

    public MerchantUpdateRequest() {}

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public MerchantStatus getStatus()             { return status; }
    public void setStatus(MerchantStatus status)  { this.status = status; }

    public String getBusinessType()                   { return businessType; }
    public void setBusinessType(String businessType)  { this.businessType = businessType; }

    public String getContactPhone()               { return contactPhone; }
    public void setContactPhone(String phone)     { this.contactPhone = phone; }

    public String getAddress()                { return address; }
    public void setAddress(String address)    { this.address = address; }
}
