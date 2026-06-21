package com.example.payment.repository;

import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByMerchantId(String merchantId);

    List<Payment> findByMerchantIdAndStatus(String merchantId, PaymentStatus status);
}
