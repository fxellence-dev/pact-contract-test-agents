package com.example.merchant.service;

import com.example.merchant.dto.*;
import com.example.merchant.exception.MerchantNotFoundException;
import com.example.merchant.exception.UnauthorizedException;
import com.example.merchant.model.Merchant;
import com.example.merchant.model.MerchantStatus;
import com.example.merchant.repository.MerchantRepository;
import com.example.merchant.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantService {

    public MerchantService(MerchantRepository merchantRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Validates merchant credentials and returns a signed JWT.
     * The JWT encodes merchantId and merchantName — used by the Payment Service.
     */
    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthRequest request) {
        Merchant merchant = merchantRepository.findByMerchantId(request.getMerchantId())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!merchant.getApiKey().equals(request.getApiKey())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!passwordEncoder.matches(request.getApiSecret(), merchant.getApiSecret())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw new UnauthorizedException("Merchant account is not active — status: " + merchant.getStatus());
        }

        String token = jwtUtil.generateToken(merchant.getMerchantId(), merchant.getName());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .merchantId(merchant.getMerchantId())
                .merchantName(merchant.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MerchantDto> getAllMerchants() {
        return merchantRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MerchantDto getMerchantById(String merchantId) {
        Merchant merchant = merchantRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found: " + merchantId));
        return toDto(merchant);
    }

    @Transactional
    public MerchantDto createMerchant(MerchantCreateRequest request) {
        if (merchantRepository.existsByMerchantId(request.getMerchantId())) {
            throw new IllegalArgumentException("Merchant ID already exists: " + request.getMerchantId());
        }
        if (merchantRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Merchant merchant = new Merchant();
        merchant.setMerchantId(request.getMerchantId());
        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setApiKey(request.getApiKey());
        merchant.setApiSecret(passwordEncoder.encode(request.getApiSecret()));
        merchant.setStatus(MerchantStatus.ACTIVE);
        merchant.setBusinessType(request.getBusinessType());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setAddress(request.getAddress());

        return toDto(merchantRepository.save(merchant));
    }

    @Transactional
    public MerchantDto updateMerchant(String merchantId, MerchantUpdateRequest request) {
        Merchant merchant = merchantRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found: " + merchantId));

        if (request.getName() != null)         merchant.setName(request.getName());
        if (request.getEmail() != null)         merchant.setEmail(request.getEmail());
        if (request.getStatus() != null)        merchant.setStatus(request.getStatus());
        if (request.getBusinessType() != null)  merchant.setBusinessType(request.getBusinessType());
        if (request.getContactPhone() != null)  merchant.setContactPhone(request.getContactPhone());
        if (request.getAddress() != null)       merchant.setAddress(request.getAddress());

        return toDto(merchantRepository.save(merchant));
    }

    @Transactional
    public void deleteMerchant(String merchantId) {
        Merchant merchant = merchantRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException("Merchant not found: " + merchantId));
        merchantRepository.delete(merchant);
    }

    private MerchantDto toDto(Merchant m) {
        return MerchantDto.builder()
                .id(m.getId())
                .merchantId(m.getMerchantId())
                .name(m.getName())
                .email(m.getEmail())
                .apiKey(m.getApiKey())
                .status(m.getStatus())
                .businessType(m.getBusinessType())
                .contactPhone(m.getContactPhone())
                .address(m.getAddress())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
