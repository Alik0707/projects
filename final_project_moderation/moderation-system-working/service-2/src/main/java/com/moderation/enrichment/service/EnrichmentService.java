package com.moderation.enrichment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moderation.enrichment.dto.EnrichmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrichmentService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String CUSTOMER_INFO_PREFIX = "customer:info:";
    private static final String ACTIVE_REQUESTS_PREFIX = "customer:requests:";
    
    public EnrichmentResponse getEnrichmentData(String customerId, String requestId) {
        log.info("Fetching enrichment data for customerId: {}, requestId: {}", customerId, requestId);
        
        try {
            // Получаем инфо о клиенте
            EnrichmentResponse.CustomerInfo customerInfo = getCustomerInfo(customerId);
            
            //  активные обращения
            List<EnrichmentResponse.ActiveRequest> activeRequests = getActiveRequests(customerId);
            
            boolean found = customerInfo != null;
            
            return EnrichmentResponse.builder()
                    .requestId(requestId)
                    .customerId(customerId)
                    .customerInfo(customerInfo)
                    .activeRequests(activeRequests != null ? activeRequests : new ArrayList<>())
                    .found(found)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error fetching enrichment data for customerId: {}", customerId, e);
            // корректный ответ даже при ошибке
            return EnrichmentResponse.builder()
                    .requestId(requestId)
                    .customerId(customerId)
                    .activeRequests(new ArrayList<>())
                    .found(false)
                    .build();
        }
    }
    
    private EnrichmentResponse.CustomerInfo getCustomerInfo(String customerId) {
        try {
            String key = CUSTOMER_INFO_PREFIX + customerId;
            String json = redisTemplate.opsForValue().get(key);
            
            if (json == null) {
                log.info("Customer info not found in cache for customerId: {}", customerId);
                return null;
            }
            
            return objectMapper.readValue(json, EnrichmentResponse.CustomerInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing customer info for customerId: {}", customerId, e);
            return null;
        }
    }
    
    private List<EnrichmentResponse.ActiveRequest> getActiveRequests(String customerId) {
        try {
            String pattern = ACTIVE_REQUESTS_PREFIX + customerId + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys == null || keys.isEmpty()) {
                log.info("No active requests found for customerId: {}", customerId);
                return new ArrayList<>();
            }
            
            List<EnrichmentResponse.ActiveRequest> requests = new ArrayList<>();
            for (String key : keys) {
                String json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    EnrichmentResponse.ActiveRequest request = 
                        objectMapper.readValue(json, EnrichmentResponse.ActiveRequest.class);
                    requests.add(request);
                }
            }
            
            return requests;
        } catch (Exception e) {
            log.error("Error fetching active requests for customerId: {}", customerId, e);
            return new ArrayList<>();
        }
    }
    public void saveCustomerInfo(String customerId, EnrichmentResponse.CustomerInfo customerInfo) {
        try {
            String key = CUSTOMER_INFO_PREFIX + customerId;
            String json = objectMapper.writeValueAsString(customerInfo);
            redisTemplate.opsForValue().set(key, json);
            log.info("Saved customer info for customerId: {}", customerId);
        } catch (JsonProcessingException e) {
            log.error("Error saving customer info for customerId: {}", customerId, e);
        }
    }
    
    public void saveActiveRequest(String customerId, EnrichmentResponse.ActiveRequest activeRequest) {
        try {
            String key = ACTIVE_REQUESTS_PREFIX + customerId + ":" + activeRequest.getRequestId();
            String json = objectMapper.writeValueAsString(activeRequest);
            redisTemplate.opsForValue().set(key, json);
            log.info("Saved active request for customerId: {}, requestId: {}", 
                customerId, activeRequest.getRequestId());
        } catch (JsonProcessingException e) {
            log.error("Error saving active request", e);
        }
    }
    
    public void removeActiveRequest(String customerId, String requestId) {
        String key = ACTIVE_REQUESTS_PREFIX + customerId + ":" + requestId;
        redisTemplate.delete(key);
        log.info("Removed active request for customerId: {}, requestId: {}", customerId, requestId);
    }
}
