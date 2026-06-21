package com.example.payment.service;

import com.example.payment.client.MerchantClient;
import com.example.payment.dto.MerchantDto;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.exception.PaymentNotFoundException;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import com.example.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final MerchantClient merchantClient;

    public PaymentService(PaymentRepository paymentRepository, MerchantClient merchantClient) {
        this.paymentRepository = paymentRepository;
        this.merchantClient = merchantClient;
    }

    /**
     * Process a new payment.
     *
     * Flow:
     *  1. The JWT filter has already validated the token and set merchantId as the principal.
     *  2. We call the Merchant Service (forwarding the same JWT) to verify the merchant is ACTIVE.
     *  3. We persist the payment and simulate processing.
     *
     * @param request            validated payment details
     * @param merchantId         extracted from JWT by the controller
     * @param authorizationHeader  original "Bearer <token>" forwarded to Merchant Service
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request,
                                          String merchantId,
                                          String authorizationHeader) {

        // ── Step 1: Verify merchant is active via Merchant Service ──────────
        MerchantDto merchant = merchantClient.getMerchant(merchantId, authorizationHeader);

        if (!"ACTIVE".equalsIgnoreCase(merchant.getStatus())) {
            throw new IllegalStateException(
                    "Merchant account is not active — cannot process payment. Status: " + merchant.getStatus());
        }

        // ── Step 2: Persist payment ──────────────────────────────────────────
        Payment payment = new Payment();
        payment.setTransactionId("txn_" + UUID.randomUUID());
        payment.setMerchantId(merchantId);
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setPayerName(request.getPayerName());
        payment.setPayerEmail(request.getPayerEmail());
        payment.setDescription(request.getDescription());
        payment.setPaymentMethod(request.getPaymentMethod());

        payment = paymentRepository.save(payment);

        // ── Step 3: Simulate payment processing ─────────────────────────────
        // In production this would call a payment gateway (Stripe, Adyen, etc.)
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setProcessorReference("AUTH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment = paymentRepository.save(payment);

        log.info("Payment {} processed — merchant={} amount={} {}",
                payment.getTransactionId(), merchantId, payment.getAmount(), payment.getCurrency());

        return toResponse(payment, merchant);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByMerchant(String merchantId, String authorizationHeader) {
        MerchantDto merchant = merchantClient.getMerchant(merchantId, authorizationHeader);
        return paymentRepository.findByMerchantId(merchantId)
                .stream()
                .map(p -> toResponse(p, merchant))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String transactionId, String merchantId, String authorizationHeader) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + transactionId));

        // Ensure the requesting merchant owns this payment
        if (!payment.getMerchantId().equals(merchantId)) {
            throw new PaymentNotFoundException("Payment not found: " + transactionId);
        }

        MerchantDto merchant = merchantClient.getMerchant(merchantId, authorizationHeader);
        return toResponse(payment, merchant);
    }

    @Transactional
    public PaymentResponse cancelPayment(String transactionId, String merchantId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + transactionId));

        if (!payment.getMerchantId().equals(merchantId)) {
            throw new PaymentNotFoundException("Payment not found: " + transactionId);
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed payment — use refund instead");
        }
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("Payment is already cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment = paymentRepository.save(payment);

        // Return without extra merchant call
        MerchantDto placeholder = new MerchantDto();
        placeholder.setMerchantId(merchantId);
        placeholder.setName("");
        return toResponse(payment, placeholder);
    }

    private PaymentResponse toResponse(Payment p, MerchantDto merchant) {
        return PaymentResponse.builder()
                .id(p.getId())
                .transactionId(p.getTransactionId())
                .merchantId(p.getMerchantId())
                .merchantName(merchant.getName())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus())
                .paymentMethod(p.getPaymentMethod())
                .payerName(p.getPayerName())
                .payerEmail(p.getPayerEmail())
                .description(p.getDescription())
                .processorReference(p.getProcessorReference())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
