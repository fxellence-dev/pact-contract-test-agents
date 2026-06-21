package com.example.payment.controller;

import com.example.payment.dto.ErrorResponse;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment acceptance and processing")
public class PaymentController {

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    private final PaymentService paymentService;

    @Operation(
        summary = "Process a new payment",
        description = """
            Submits a payment for processing.
            
            - The `merchantId` is **automatically extracted from the JWT** — do not include it in the body.
            - The service verifies the merchant account is active by calling the Merchant Service.
            - A `transactionId` is generated and returned in the response.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment processed successfully",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Merchant account not active",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "Merchant Service unavailable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String authorizationHeader,
            Authentication authentication) {

        String merchantId = (String) authentication.getPrincipal();
        PaymentResponse response = paymentService.processPayment(request, merchantId, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "List payments for the authenticated merchant",
        description = "Returns all payments belonging to the merchant identified by the JWT."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment list returned"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @RequestHeader("Authorization") String authorizationHeader,
            Authentication authentication) {

        String merchantId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.getPaymentsByMerchant(merchantId, authorizationHeader));
    }

    @Operation(
        summary = "Get payment by transaction ID",
        description = "Retrieves a specific payment. Only the owning merchant can access it."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment found",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Payment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "Transaction ID returned when the payment was created",
                       example = "txn_550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String transactionId,
            @RequestHeader("Authorization") String authorizationHeader,
            Authentication authentication) {

        String merchantId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.getPayment(transactionId, merchantId, authorizationHeader));
    }

    @Operation(
        summary = "Cancel a payment",
        description = "Cancels a pending or processing payment. Completed payments cannot be cancelled (use refund)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment cancelled",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Payment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Payment cannot be cancelled in its current state",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{transactionId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @Parameter(description = "Transaction ID to cancel")
            @PathVariable String transactionId,
            Authentication authentication) {

        String merchantId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(paymentService.cancelPayment(transactionId, merchantId));
    }
}
