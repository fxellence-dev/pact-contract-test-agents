package com.example.merchant.controller;

import com.example.merchant.dto.*;
import com.example.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@Tag(name = "Merchants", description = "Merchant account management and authentication")
public class MerchantController {

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    private final MerchantService merchantService;

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC — No JWT required
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(
        summary = "Authenticate merchant — get JWT token",
        description = """
            Validates merchantId + apiKey + apiSecret.
            Returns a signed JWT that must be passed as `Authorization: Bearer <token>`
            on all other endpoints and on the Payment Service.
            """,
        security = {}   // Override global BearerAuth — this endpoint is public
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements   // empty — public endpoint
    @PostMapping("/auth/token")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(merchantService.authenticate(request));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROTECTED — Valid JWT required
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(summary = "List all merchants", description = "Returns all registered merchant accounts.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Merchant list returned"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<MerchantDto>> getAllMerchants() {
        return ResponseEntity.ok(merchantService.getAllMerchants());
    }

    @Operation(
        summary = "Get merchant by ID",
        description = "Returns a single merchant. Called internally by the Payment Service using the JWT extracted merchantId."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Merchant found",
            content = @Content(schema = @Schema(implementation = MerchantDto.class))),
        @ApiResponse(responseCode = "404", description = "Merchant not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{merchantId}")
    public ResponseEntity<MerchantDto> getMerchant(
            @Parameter(description = "Merchant business identifier", example = "MERCH001")
            @PathVariable String merchantId) {
        return ResponseEntity.ok(merchantService.getMerchantById(merchantId));
    }

    @Operation(summary = "Create a new merchant account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Merchant created",
            content = @Content(schema = @Schema(implementation = MerchantDto.class))),
        @ApiResponse(responseCode = "409", description = "Merchant ID or email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MerchantDto> createMerchant(@Valid @RequestBody MerchantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(merchantService.createMerchant(request));
    }

    @Operation(summary = "Update a merchant account", description = "Partial update — only provided fields are changed.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Merchant updated",
            content = @Content(schema = @Schema(implementation = MerchantDto.class))),
        @ApiResponse(responseCode = "404", description = "Merchant not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{merchantId}")
    public ResponseEntity<MerchantDto> updateMerchant(
            @Parameter(description = "Merchant business identifier", example = "MERCH001")
            @PathVariable String merchantId,
            @Valid @RequestBody MerchantUpdateRequest request) {
        return ResponseEntity.ok(merchantService.updateMerchant(merchantId, request));
    }

    @Operation(summary = "Delete a merchant account")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Merchant deleted"),
        @ApiResponse(responseCode = "404", description = "Merchant not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> deleteMerchant(
            @Parameter(description = "Merchant business identifier", example = "MERCH001")
            @PathVariable String merchantId) {
        merchantService.deleteMerchant(merchantId);
        return ResponseEntity.noContent().build();
    }
}
