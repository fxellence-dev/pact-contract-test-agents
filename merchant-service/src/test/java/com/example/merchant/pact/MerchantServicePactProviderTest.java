package com.example.merchant.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import com.example.merchant.model.Merchant;
import com.example.merchant.model.MerchantStatus;
import com.example.merchant.repository.MerchantRepository;
import com.example.merchant.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Pact provider verification test for the Merchant Service.
 *
 * Fetches the pact published by the Payment Service from the Pact Broker and
 * replays every interaction against the real Merchant Service running on a
 * random port.
 *
 * Auth strategy: JwtUtil is replaced with a @MockBean so the real
 * JwtAuthenticationFilter uses controllable token validation:
 *   - "test-jwt-token"  → valid   → request is authenticated → 200 / 404
 *   - "invalid-token"   → invalid → security context stays empty → 401
 *
 * To publish results to the broker (CI only):
 *   -Dpact.verifier.publishResults=true
 *   -Dpact.provider.version=$GIT_COMMIT
 *   -Dpact.provider.branch=$GIT_BRANCH
 */
@Provider("merchant-service")
@PactBroker(
    url = "${PACT_BROKER_URL:http://localhost:9292}",
    authentication = @PactBrokerAuth(
        username = "${PACT_BROKER_USERNAME:admin}",
        password = "${PACT_BROKER_PASSWORD:admin}"
    )
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MerchantServicePactProviderTest {

    @LocalServerPort
    private int port;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));

        // Token used by consumer pact for all authorised interactions
        when(jwtUtil.isTokenValid(eq("test-jwt-token"))).thenReturn(true);
        when(jwtUtil.extractMerchantId(eq("test-jwt-token"))).thenReturn("MERCH001");
        when(jwtUtil.extractMerchantName(eq("test-jwt-token"))).thenReturn("Acme Corp");

        // Token used by the unauthorised interaction — must be rejected
        when(jwtUtil.isTokenValid(eq("invalid-token"))).thenReturn(false);
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    // -------------------------------------------------------------------------
    // Provider States — mirror the states defined in MerchantClientPactTest
    // -------------------------------------------------------------------------

    @State("a merchant with id MERCH001 exists and is ACTIVE")
    void merch001Active() {
        merchantRepository.deleteAll();
        merchantRepository.save(buildMerchant("MERCH001", "Acme Corp", MerchantStatus.ACTIVE));
    }

    @State("a merchant with id MERCH001 exists and is INACTIVE")
    void merch001Inactive() {
        merchantRepository.deleteAll();
        merchantRepository.save(buildMerchant("MERCH001", "Acme Corp", MerchantStatus.INACTIVE));
    }

    @State("a merchant with id MERCH001 exists and is SUSPENDED")
    void merch001Suspended() {
        merchantRepository.deleteAll();
        merchantRepository.save(buildMerchant("MERCH001", "Acme Corp", MerchantStatus.SUSPENDED));
    }

    @State("merchant MERCH999 does not exist")
    void merch999NotFound() {
        merchantRepository.findByMerchantId("MERCH999").ifPresent(merchantRepository::delete);
    }

    @State("the request carries an invalid JWT")
    void invalidJwtState() {
        // No data setup needed.
        // JwtUtil mock returns false for "invalid-token", so JwtAuthenticationFilter
        // leaves the security context empty and Spring Security returns 401.
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private @NonNull Merchant buildMerchant(String merchantId, String name, MerchantStatus status) {
        Merchant m = new Merchant();
        m.setMerchantId(merchantId);
        m.setName(name);
        m.setEmail(merchantId.toLowerCase() + "@test.example.com");
        m.setApiKey("mk_test_key");
        m.setApiSecret(passwordEncoder.encode("test_secret"));
        m.setStatus(status);
        m.setBusinessType("RETAIL");
        return m;
    }
}
