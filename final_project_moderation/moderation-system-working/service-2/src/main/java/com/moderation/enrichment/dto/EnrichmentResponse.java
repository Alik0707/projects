package com.moderation.enrichment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichmentResponse {
    private String requestId;
    private String customerId;
    private CustomerInfo customerInfo;
    private List<ActiveRequest> activeRequests;
    private boolean found;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String tier;
        private String preferredLanguage;
        @JsonProperty("isBlocked")
        private boolean isBlocked;
        private String registrationDate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActiveRequest {
        private String requestId;
        private String category;
        private String status;
        private String createdAt;
    }
}
