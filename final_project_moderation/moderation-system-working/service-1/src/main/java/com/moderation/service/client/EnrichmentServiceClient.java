package com.moderation.service.client;

import com.moderation.service.model.EnrichmentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${enrichment.service.url}")
    private String enrichmentServiceUrl;
    
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public EnrichmentData getEnrichmentData(String customerId, String requestId) {
        log.info("Calling enrichment service for customerId: {}, requestId: {}", customerId, requestId);
        
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(enrichmentServiceUrl)
                    .path("/api/enrichment/{customerId}")
                    .queryParam("requestId", requestId)
                    .buildAndExpand(customerId)
                    .toUriString();
            
            EnrichmentData response = restTemplate.getForObject(url, EnrichmentData.class);
            log.info("Successfully received enrichment data for customerId: {}", customerId);
            
            return response;
            
        } catch (RestClientException e) {
            log.error("Error calling enrichment service for customerId: {}, attempt will retry", customerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling enrichment service for customerId: {}", customerId, e);
            // Возвращаем пустые данные ошибках
            return EnrichmentData.builder()
                    .requestId(requestId)
                    .customerId(customerId)
                    .found(false)
                    .build();
        }
    }
}
