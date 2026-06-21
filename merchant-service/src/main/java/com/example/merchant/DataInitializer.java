package com.example.merchant;

import com.example.merchant.model.Merchant;
import com.example.merchant.model.MerchantStatus;
import com.example.merchant.repository.MerchantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(MerchantRepository merchantRepository, PasswordEncoder passwordEncoder) {
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (merchantRepository.count() == 0) {
            seed("MERCH001", "Acme Corp",       "acme@example.com",
                 "mk_live_abc123", "ms_live_secret001", "RETAIL",
                 "+1-555-0101", "100 Commerce St, New York, NY 10001");

            seed("MERCH002", "TechStore Inc",   "techstore@example.com",
                 "mk_live_def456", "ms_live_secret002", "ELECTRONICS",
                 "+1-555-0202", "200 Tech Ave, San Francisco, CA 94105");

            seed("MERCH003", "Fashion Hub",     "fashionhub@example.com",
                 "mk_live_ghi789", "ms_live_secret003", "FASHION",
                 "+1-555-0303", "300 Style Blvd, Los Angeles, CA 90001");

            log.info("=================================================================");
            log.info("  Merchant Service — test data seeded successfully");
            log.info("  Merchants: MERCH001 / MERCH002 / MERCH003");
            log.info("  Swagger UI : http://localhost:8081/swagger-ui.html");
            log.info("  H2 Console : http://localhost:8081/h2-console");
            log.info("=================================================================");
        }
    }

    private void seed(String merchantId, String name, String email,
                      String apiKey, String apiSecret, String businessType,
                      String phone, String address) {
        Merchant m = new Merchant();
        m.setMerchantId(merchantId);
        m.setName(name);
        m.setEmail(email);
        m.setApiKey(apiKey);
        m.setApiSecret(passwordEncoder.encode(apiSecret));
        m.setStatus(MerchantStatus.ACTIVE);
        m.setBusinessType(businessType);
        m.setContactPhone(phone);
        m.setAddress(address);
        merchantRepository.save(m);
    }
}
