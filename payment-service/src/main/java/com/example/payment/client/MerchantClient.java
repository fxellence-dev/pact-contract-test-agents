package com.example.payment.client;

import com.example.payment.dto.MerchantDto;
import com.example.payment.exception.MerchantServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for the Merchant Service.
 * Forwards the original JWT so the merchant service can authenticate the call.
 */
@Component
public class MerchantClient {

    private static final Logger log = LoggerFactory.getLogger(MerchantClient.class);

    private final RestTemplate restTemplate;

    @Value("${merchant.service.url}")
    private String merchantServiceUrl;

    public MerchantClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches merchant data from the Merchant Service.
     *
     * @param merchantId        business merchantId extracted from the JWT
     * @param authorizationHeader  the original "Bearer <token>" header to forward
     */
    public MerchantDto getMerchant(String merchantId, String authorizationHeader) {
        String url = merchantServiceUrl + "/api/merchants/" + merchantId;
        log.debug("Calling Merchant Service: GET {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<MerchantDto> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, MerchantDto.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new MerchantServiceException("Merchant not found in Merchant Service: " + merchantId);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new MerchantServiceException("Unauthorized when calling Merchant Service — check JWT validity");
        } catch (Exception e) {
            log.error("Error calling Merchant Service for merchantId={}: {}", merchantId, e.getMessage());
            throw new MerchantServiceException("Failed to retrieve merchant data: " + e.getMessage(), e);
        }
    }
}
